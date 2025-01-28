package io.dogsbean.twentyquestions.scoreboard;

import io.dogsbean.twentyquestions.Main;
import io.dogsbean.twentyquestions.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private final Main plugin;

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("twentyquestions", "dummy");
        obj.setDisplayName("§6§l스무고개");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (plugin.getGameManager().getGameState() != GameState.PLAYING) {
            return;
        }

        Player questioner = plugin.getGameManager().getQuestioner();
        if (questioner != null) {
            Score questionerScore = obj.getScore("§f출제자: §6" + questioner.getDisplayName());
            questionerScore.setScore(0);
        }

        Player currentPlayer = plugin.getGameManager().getCurrentPlayer();
        if (currentPlayer != null) {
            Score currentPlayerScore = obj.getScore("§f질문자: §6" + currentPlayer.getDisplayName());
            currentPlayerScore.setScore(1);
        }

        String topic = plugin.getGameManager().getTopic();
        if (topic != null) {
            Score topicScore = obj.getScore("§f주제: §6" + topic);
            topicScore.setScore(2);
        }

        Integer remainingQuestions = plugin.getGameManager().getRemainingQuestions();
        Score remainingQuestionsScore = obj.getScore("§f남은 기회: §6" + remainingQuestions);
        remainingQuestionsScore.setScore(3);

        Score emptyLine = obj.getScore("§f");
        emptyLine.setScore(4);

        player.setScoreboard(board);
    }
}
