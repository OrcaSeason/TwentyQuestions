package io.dogsbean.twentyquestions.commands;

import io.dogsbean.twentyquestions.Main;
import io.dogsbean.twentyquestions.game.GameManager;
import io.dogsbean.twentyquestions.game.GameState;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameCommandHandler {
    private final Main plugin;
    private final GameManager gameManager;

    public GameCommandHandler(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public boolean handleCommand(Player player, String command, String[] args) {
        switch (command.toLowerCase()) {
            case "게임":
                if (args.length == 1 && args[0].equals("시작")) {
                    return gameManager.startGame(player);
                }
                break;

            case "주제":
                if (args.length >= 2 && args[0].equals("설정")) {
                    return gameManager.setTopic(player, String.join(" ", args).substring(3));
                }
                break;

            case "정답":
                if (args.length >= 1) {
                    if (args[0].equals("설정") && args.length >= 2) {
                        return handleAnswerSet(player, String.join(" ", args).substring(3));
                    } else {
                        return handleAnswerGuess(player, String.join(" ", args));
                    }
                }
                break;

            case "질문":
                if (args.length >= 1) {
                    return handleQuestion(player, String.join(" ", args));
                }
                break;

            case "기회":
                if (args.length == 1 && args[0].equals("차감")) {
                    return handleDeductChance(player);
                }
                break;

            case "정답공개":
                return handleRevealAnswer(player);

            case "응답":
                if (args.length == 1 && (args[0].equals("O") || args[0].equals("X"))) {
                    return handleAnswer(player, args[0]);
                }
                break;
            case "힌트":
                if (args.length >= 1) {
                    if (args[0].equals("제공") && args.length >= 2) {
                        return handleHintProvide(player, String.join(" ", args).substring(3));
                    } else if (args[0].equals("거절")) {
                        return handleHintReject(player);
                    }
                }
                break;
        }
        return false;
    }

    private boolean handleAnswerSet(Player player, String answer) {
        return gameManager.setAnswer(player, answer);
    }

    private boolean handleAnswerGuess(Player player, String guess) {
        if (gameManager.getGameState() != GameState.PLAYING ||
                player == gameManager.getQuestioner() ||
                player != gameManager.getCurrentPlayer()) {
            player.sendMessage(ChatColor.RED + "지금은 정답을 맞출 수 없습니다!");
            return true;
        }

        gameManager.resetPlayerTimeout();

        if (gameManager.checkAnswer(guess)) {
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + "님이 정답을 맞추셨습니다!");
            gameManager.handleCorrectAnswer();
            gameManager.endGame(false);
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + player.getName() + "님의 정답이 틀렸습니다!");
            gameManager.handleWrongAnswer();
            handleDeductChance(gameManager.getQuestioner());
        }

        return true;
    }

    private boolean handleQuestion(Player player, String question) {
        if (gameManager.getGameState() != GameState.PLAYING ||
                player == gameManager.getQuestioner() ||
                player != gameManager.getCurrentPlayer()) {
            player.sendMessage(ChatColor.RED + "지금은 질문을 할 수 없습니다!");
            return true;
        }

        Bukkit.broadcastMessage(ChatColor.WHITE + "◾ " + ChatColor.GREEN + player.getName() + "의 질문: \n" + ChatColor.YELLOW + ChatColor.BOLD + "'" + question + "'");
        gameManager.hasAskedQuestion = true;
        gameManager.cancelResetTimer();

        TextComponent message = new TextComponent(ChatColor.GOLD + "답변하기: ");

        TextComponent yesButton = new TextComponent(ChatColor.GREEN + "[O]");
        yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.GREEN + "질문이 정답과 관련이 있을 경우 클릭하세요.").create()));
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/응답 O"));

        TextComponent separator = new TextComponent(" ");

        TextComponent noButton = new TextComponent(ChatColor.RED + "[X]");
        noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.RED + "질문이 정답과 관련이 없을 경우 클릭하세요.").create()));
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/응답 X"));

        message.addExtra(yesButton);
        message.addExtra(separator);
        message.addExtra(noButton);

        gameManager.getQuestioner().spigot().sendMessage(message);
        return true;
    }

    private boolean handleAnswer(Player player, String answer) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(ChatColor.RED + "지금은 답변을 할 수 없습니다!");
            return true;
        }

        Bukkit.broadcastMessage(ChatColor.WHITE + "◾ 출제자의 답변: " + ChatColor.GREEN + answer);
        if (answer.equals("X")) {
            gameManager.handleWrongAnswer();
        } else {
            gameManager.handleCorrectAnswer();
        }
        handleDeductChance(player);
        return true;
    }

    private boolean handleHintProvide(Player player, String hint) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(ChatColor.RED + "지금은 힌트를 제공할 수 없습니다!");
            return true;
        }

        gameManager.provideHint(hint);
        return true;
    }

    private boolean handleHintReject(Player player) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(ChatColor.RED + "지금은 힌트 제공을 거절할 수 없습니다!");
            return true;
        }

        gameManager.rejectHint();
        return true;
    }

    private boolean handleDeductChance(Player player) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(ChatColor.RED + "지금은 기회를 차감할 수 없습니다!");
            return true;
        }

        gameManager.deductQuestion();

        if (gameManager.isGameOver()) {
            gameManager.endGame(true);
        } else {
            gameManager.nextPlayer();
        }

        return true;
    }

    private boolean handleRevealAnswer(Player player) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(ChatColor.RED + "지금은 정답을 공개할 수 없습니다!");
            return true;
        }

        gameManager.endGame(false);
        return true;
    }
}