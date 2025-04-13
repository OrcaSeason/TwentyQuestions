package io.dogsbean.twentyquestions.game;

import io.dogsbean.twentyquestions.Main;
import io.dogsbean.twentyquestions.lang.LanguageManager;
import io.dogsbean.twentyquestions.scoreboard.ScoreboardManager;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private final Main plugin;
    private final LanguageManager languageManager;
    @Getter
    private GameState gameState = GameState.INACTIVE;
    @Getter
    private Player questioner = null;
    @Getter
    private Player currentPlayer = null;
    private Player previousPlayer = null;
    private final List<Player> nonQuestioners = new ArrayList<>();
    @Getter
    private String topic = null;
    private String answer = null;
    private int remainingQuestions = 20;
    @Getter
    private List<Player> players = new ArrayList<>();
    private BukkitRunnable currentPlayerTimeoutTask;
    private final ScoreboardManager scoreboardManager;
    public boolean hasAskedQuestion = false;
    private int consecutiveWrongCount = 0;

    public GameManager(Main plugin, ScoreboardManager scoreboardManager, LanguageManager languageManager) {
        this.plugin = plugin;
        this.scoreboardManager = scoreboardManager;
        this.languageManager = languageManager;
    }

    public Integer getRemainingQuestions() {
        return remainingQuestions;
    }

    public boolean startGame(Player initiator) {
        if (gameState != GameState.INACTIVE) {
            initiator.sendMessage(languageManager.getMessage("game-already-in-progress"));
            return false;
        }

        players.clear();
        players.addAll(Bukkit.getOnlinePlayers());

        if (players.size() < 2) {
            initiator.sendMessage(languageManager.getMessage("not-enough-players"));
            return false;
        }

        gameState = GameState.SETUP;
        questioner = players.get(new Random().nextInt(players.size()));
        remainingQuestions = 20;
        topic = null;
        answer = null;

        nonQuestioners.addAll(players);
        nonQuestioners.remove(questioner);

        players.forEach(scoreboardManager::updateScoreboard);
        Bukkit.broadcastMessage(languageManager.getMessage("game-started"));
        Bukkit.broadcastMessage(languageManager.getMessage("questioner-announce", "%questioner%", questioner.getName()));
        questioner.sendMessage(languageManager.getMessage("set-topic-prompt"));
        return true;
    }

    public boolean setTopic(Player player, String topic) {
        if (gameState != GameState.SETUP || player != questioner) {
            player.sendMessage(languageManager.getMessage("cannot-set-topic"));
            return false;
        }

        this.topic = topic;
        Bukkit.broadcastMessage(languageManager.getMessage("topic-set"));
        questioner.sendMessage(languageManager.getMessage("set-answer-prompt"));
        players.forEach(scoreboardManager::updateScoreboard);
        return true;
    }

    public boolean setAnswer(Player player, String answer) {
        if (gameState != GameState.SETUP || player != questioner || topic == null) {
            player.sendMessage(languageManager.getMessage("cannot-set-answer"));
            return false;
        }

        this.answer = answer;
        players.forEach(scoreboardManager::updateScoreboard);
        startGameCountdown();
        return true;
    }

    private void startGameCountdown() {
        Bukkit.broadcastMessage(languageManager.getMessage("game-starting-soon"));

        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count > 0) {
                    Bukkit.broadcastMessage(languageManager.getMessage("countdown", "%count%", String.valueOf(count)));
                    count--;
                } else {
                    gameState = GameState.PLAYING;
                    Bukkit.broadcastMessage(languageManager.getMessage("game-started-playing"));
                    Bukkit.broadcastMessage(languageManager.getMessage("topic-announce", "%topic%", topic));
                    players.forEach(scoreboardManager::updateScoreboard);
                    nextPlayer();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void delegateTurnTo(Player player) {
        currentPlayer = player;
        announceCurrentPlayer();
        resetPlayerTimeout();
    }

    public void nextPlayer() {
        if (currentPlayerTimeoutTask != null) {
            currentPlayerTimeoutTask.cancel();
        }

        if (players.isEmpty()) {
            Bukkit.broadcastMessage(languageManager.getMessage("error-no-players"));
            endGame(true);
            return;
        }

        List<Player> availablePlayers = new ArrayList<>(nonQuestioners);
        availablePlayers.remove(previousPlayer);

        if (availablePlayers.isEmpty()) {
            Bukkit.broadcastMessage(languageManager.getMessage("error-no-available-players"));
            endGame(true);
            return;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(nonQuestioners.size());
        currentPlayer = nonQuestioners.get(randomIndex);

        previousPlayer = currentPlayer;

        announceCurrentPlayer();
        resetPlayerTimeout();
        players.forEach(scoreboardManager::updateScoreboard);
    }

    private void announceCurrentPlayer() {
        Bukkit.broadcastMessage(languageManager.getMessage("current-player-announce", "%player%", getCurrentPlayer().getName()));
    }

    public void resetPlayerTimeout() {
        if (currentPlayerTimeoutTask != null) {
            currentPlayerTimeoutTask.cancel();
        }

        currentPlayerTimeoutTask = new BukkitRunnable() {
            @Override
            public void run() {
                Player currentPlayer = getCurrentPlayer();
                Bukkit.broadcastMessage(languageManager.getMessage("timeout-message", "%player%", currentPlayer.getName()));
                nextPlayer();
            }
        };

        currentPlayerTimeoutTask.runTaskLater(plugin, 20L * 60);
    }

    public void cancelResetTimer() {
        if (currentPlayerTimeoutTask != null) {
            currentPlayerTimeoutTask.cancel();
        }
    }

    public void endGame(boolean questionerWin) {
        if (questionerWin) {
            Bukkit.broadcastMessage(languageManager.getMessage("questioner-win", "%questioner%", questioner.getName()));
        } else {
            Bukkit.broadcastMessage(languageManager.getMessage("players-win"));
        }
        Bukkit.broadcastMessage(languageManager.getMessage("answer-reveal", "%answer%", answer));
        gameState = GameState.INACTIVE;
        cancelResetTimer();
        players.forEach(scoreboardManager::updateScoreboard);
    }

    public boolean checkAnswer(String guess) {
        return guess.equalsIgnoreCase(answer);
    }

    public void deductQuestion() {
        remainingQuestions--;
        Bukkit.broadcastMessage(languageManager.getMessage("remaining-questions", "%count%", String.valueOf(remainingQuestions)));
    }

    public void handleWrongAnswer() {
        consecutiveWrongCount++;
        if (consecutiveWrongCount >= 5) {
            sendHintPrompt();
            consecutiveWrongCount = 0;
        }
    }

    public void handleCorrectAnswer() {
        consecutiveWrongCount = 0;
    }

    private void sendHintPrompt() {
        TextComponent message = new TextComponent(languageManager.getMessage("hint-prompt"));

        TextComponent yesButton = new TextComponent(languageManager.getMessage("hint-yes"));
        yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(languageManager.getMessage("hint-yes-hover")).create()));
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hint provide"));

        TextComponent separator = new TextComponent(" ");

        TextComponent noButton = new TextComponent(languageManager.getMessage("hint-no"));
        noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(languageManager.getMessage("hint-no-hover")).create()));
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hint deny"));

        message.addExtra(yesButton);
        message.addExtra(separator);
        message.addExtra(noButton);

        questioner.spigot().sendMessage(message);
    }

    public void provideHint(String hint) {
        Bukkit.broadcastMessage(languageManager.getMessage("hint-broadcast", "%hint%", hint));
    }

    public void rejectHint() {
        Bukkit.broadcastMessage(languageManager.getMessage("hint-rejected"));
    }

    public boolean isGameOver() {
        return remainingQuestions <= 0;
    }
}