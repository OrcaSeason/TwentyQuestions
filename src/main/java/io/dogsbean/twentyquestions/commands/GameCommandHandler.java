package io.dogsbean.twentyquestions.commands;

import io.dogsbean.twentyquestions.Main;
import io.dogsbean.twentyquestions.game.GameManager;
import io.dogsbean.twentyquestions.game.GameState;
import io.dogsbean.twentyquestions.lang.LanguageManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameCommandHandler {
    private final Main plugin;
    private final GameManager gameManager;
    private final LanguageManager languageManager;

    public GameCommandHandler(Main plugin, GameManager gameManager, LanguageManager languageManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.languageManager = languageManager;
    }

    public boolean handleCommand(Player player, String command, String[] args) {
        switch (command.toLowerCase()) {
            case "게임":
            case "game":
                if (args.length == 1 && (args[0].equals("시작") || args[0].equals("start"))) {
                    return gameManager.startGame(player);
                }
                break;

            case "주제":
            case "topic":
                if (args.length >= 2 && (args[0].equals("설정") || args[0].equals("set"))) {
                    return gameManager.setTopic(player, String.join(" ", args).substring(args[0].length() + 1));
                }
                break;

            case "정답":
            case "answer":
                if (args.length >= 1) {
                    if ((args[0].equals("설정") || args[0].equals("set")) && args.length >= 2) {
                        return handleAnswerSet(player, String.join(" ", args).substring(args[0].length() + 1));
                    } else {
                        return handleAnswerGuess(player, String.join(" ", args));
                    }
                }
                break;

            case "질문":
            case "question":
                if (args.length >= 1) {
                    return handleQuestion(player, String.join(" ", args));
                }
                break;

            case "기회":
            case "chance":
                if (args.length == 1 && (args[0].equals("차감") || args[0].equals("deduct"))) {
                    return handleDeductChance(player);
                }
                break;

            case "정답공개":
            case "reveal":
                return handleRevealAnswer(player);

            case "응답":
            case "respond":
                if (args.length == 1 && (args[0].equals("O") || args[0].equals("X") || args[0].equals("Yes") || args[0].equals("No"))) {
                    return handleAnswer(player, args[0].equals("O") || args[0].equals("Yes") ? "Yes" : "No");
                }
                break;

            case "힌트":
            case "hint":
                if (args.length >= 1) {
                    if ((args[0].equals("제공") || args[0].equals("provide")) && args.length >= 2) {
                        return handleHintProvide(player, String.join(" ", args).substring(args[0].length() + 1));
                    } else if (args[0].equals("거절") || args[0].equals("deny")) {
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
            player.sendMessage(languageManager.getMessage("cannot-guess-answer"));
            return true;
        }

        gameManager.resetPlayerTimeout();

        if (gameManager.checkAnswer(guess)) {
            Bukkit.broadcastMessage(languageManager.getMessage("correct-answer", "%player%", player.getName()));
            gameManager.handleCorrectAnswer();
            gameManager.endGame(false);
        } else {
            Bukkit.broadcastMessage(languageManager.getMessage("wrong-answer", "%player%", player.getName()));
            gameManager.handleWrongAnswer();
            handleDeductChance(gameManager.getQuestioner());
        }

        return true;
    }

    private boolean handleQuestion(Player player, String question) {
        if (gameManager.getGameState() != GameState.PLAYING ||
                player == gameManager.getQuestioner() ||
                player != gameManager.getCurrentPlayer()) {
            player.sendMessage(languageManager.getMessage("cannot-ask-question"));
            return true;
        }

        Bukkit.broadcastMessage(languageManager.getMessage("question-broadcast", "%player%", player.getName(), "%question%", question));
        gameManager.hasAskedQuestion = true;
        gameManager.cancelResetTimer();

        TextComponent message = new TextComponent(languageManager.getMessage("answer-prompt"));

        TextComponent yesButton = new TextComponent(languageManager.getMessage("answer-yes"));
        yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(languageManager.getMessage("answer-yes-hover")).create()));
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/respond Yes"));

        TextComponent separator = new TextComponent(" ");

        TextComponent noButton = new TextComponent(languageManager.getMessage("answer-no"));
        noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(languageManager.getMessage("answer-no-hover")).create()));
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/respond No"));

        message.addExtra(yesButton);
        message.addExtra(separator);
        message.addExtra(noButton);

        gameManager.getQuestioner().spigot().sendMessage(message);
        return true;
    }

    private boolean handleAnswer(Player player, String answer) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(languageManager.getMessage("cannot-answer"));
            return true;
        }

        Bukkit.broadcastMessage(languageManager.getMessage("answer-broadcast", "%answer%", answer));
        if (answer.equals("No")) {
            gameManager.handleWrongAnswer();
        } else {
            gameManager.handleCorrectAnswer();
        }
        handleDeductChance(player);
        return true;
    }

    private boolean handleHintProvide(Player player, String hint) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(languageManager.getMessage("cannot-provide-hint"));
            return true;
        }

        gameManager.provideHint(hint);
        return true;
    }

    private boolean handleHintReject(Player player) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(languageManager.getMessage("cannot-reject-hint"));
            return true;
        }

        gameManager.rejectHint();
        return true;
    }

    private boolean handleDeductChance(Player player) {
        if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getQuestioner()) {
            player.sendMessage(languageManager.getMessage("cannot-deduct-chance"));
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
            player.sendMessage(languageManager.getMessage("cannot-reveal-answer"));
            return true;
        }

        gameManager.endGame(false);
        return true;
    }
}