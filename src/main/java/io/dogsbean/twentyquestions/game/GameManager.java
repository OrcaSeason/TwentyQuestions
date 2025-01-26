package io.dogsbean.twentyquestions.game;

import io.dogsbean.twentyquestions.Main;
import io.dogsbean.twentyquestions.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private final Main plugin;
    private GameState gameState = GameState.INACTIVE;
    private Player questioner = null;
    private Player currentPlayer = null;
    private Player previousPlayer = null;
    private final List<Player> nonQuestioners = new ArrayList<>();
    private String topic = null;
    private String answer = null;
    private int remainingQuestions = 20;
    private List<Player> players = new ArrayList<>();
    private BukkitRunnable currentPlayerTimeoutTask;
    private final ScoreboardManager scoreboardManager;
    public boolean hasAskedQuestion = false;
    private boolean hasSetAnswer = false;

    public GameManager(Main plugin, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.scoreboardManager = scoreboardManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getQuestioner() {
        return questioner;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getRemainingQuestions() {
        return remainingQuestions;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean startGame(Player initiator) {
        if (gameState != GameState.INACTIVE) {
            initiator.sendMessage(ChatColor.RED + "게임이 이미 진행 중입니다!");
            return false;
        }

        players.clear();
        players.addAll(Bukkit.getOnlinePlayers());

        if (players.size() < 2) {
            initiator.sendMessage(ChatColor.RED + "게임을 시작하기 위해서는 최소 2명의 플레이어가 필요합니다!");
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
        Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "스무고개가 시작되었습니다!");
        Bukkit.broadcastMessage(ChatColor.WHITE + "◾ 출제자: " + ChatColor.GREEN + questioner.getName());
        questioner.sendMessage(ChatColor.YELLOW + "'/주제 설정 <주제>'로 주제를 설정해주세요.");
        return true;
    }

    public boolean setTopic(Player player, String topic) {
        if (gameState != GameState.SETUP || player != questioner) {
            player.sendMessage(ChatColor.RED + "지금은 주제를 설정할 수 없습니다!");
            return false;
        }

        this.topic = topic;
        Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "주제가 설정되었습니다!");
        questioner.sendMessage(ChatColor.YELLOW + "'/정답 설정 <정답>'으로 정답을 설정해주세요.");
        players.forEach(scoreboardManager::updateScoreboard);
        return true;
    }

    public boolean setAnswer(Player player, String answer) {
        if (gameState != GameState.SETUP || player != questioner || topic == null) {
            player.sendMessage(ChatColor.RED + "지금은 정답을 설정할 수 없습니다!");
            return false;
        }

        this.answer = answer;
        players.forEach(scoreboardManager::updateScoreboard);
        startGameCountdown();
        return true;
    }

    private void startGameCountdown() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 곧 시작됩니다!");

        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count > 0) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + String.valueOf(count));
                    count--;
                } else {
                    gameState = GameState.PLAYING;
                    Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "게임이 시작되었습니다!");
                    Bukkit.broadcastMessage(ChatColor.WHITE + "◾ 주제: " + ChatColor.GREEN + ChatColor.BOLD + topic);
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
            Bukkit.broadcastMessage(ChatColor.RED + "[Error 1]: Couldn't find player");
            endGame(true);
            return;
        }

        List<Player> availablePlayers = new ArrayList<>(nonQuestioners);
        availablePlayers.remove(previousPlayer);

        if (availablePlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + "[Error 2]: Couldn't find player excluding previous player");
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
        Bukkit.broadcastMessage(ChatColor.WHITE + "◾ 현재 차례: " + ChatColor.GREEN + getCurrentPlayer().getName());
    }

    public void resetPlayerTimeout() {
        if (currentPlayerTimeoutTask != null) {
            currentPlayerTimeoutTask.cancel();
        }

        currentPlayerTimeoutTask = new BukkitRunnable() {
            @Override
            public void run() {
                Player currentPlayer = getCurrentPlayer();
                Bukkit.broadcastMessage(ChatColor.RED + currentPlayer.getName() + "님이 1분 동안 아무 행동도 하지 않았습니다. 순서가 넘어갑니다.");
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
            Bukkit.broadcastMessage(ChatColor.GREEN + "출제자 " + questioner.getName() + "의 승리!");
        } else {
            Bukkit.broadcastMessage(ChatColor.GREEN + "참가자들의 승리!");
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "정답은 '" + answer + "'였습니다!");
        gameState = GameState.INACTIVE;
        cancelResetTimer();
        players.forEach(scoreboardManager::updateScoreboard);
    }

    public boolean checkAnswer(String guess) {
        return guess.equalsIgnoreCase(answer);
    }

    public void deductQuestion() {
        remainingQuestions--;
        Bukkit.broadcastMessage(ChatColor.YELLOW + "남은 기회: " + remainingQuestions);
    }

    public boolean isGameOver() {
        return remainingQuestions <= 0;
    }
}