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
        obj.setDisplayName(plugin.getLanguageManager().getMessage("scoreboard-title"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (plugin.getGameManager().getGameState() != GameState.PLAYING) {
            return;
        }

        Player questioner = plugin.getGameManager().getQuestioner();
        if (questioner != null) {
            Score questionerScore = obj.getScore(plugin.getLanguageManager().getMessage("scoreboard-questioner", "%questioner%", questioner.getDisplayName()));
            questionerScore.setScore(0);
        }

        Player currentPlayer = plugin.getGameManager().getCurrentPlayer();
        if (currentPlayer != null) {
            Score currentPlayerScore = obj.getScore(plugin.getLanguageManager().getMessage("scoreboard-current-player", "%player%", currentPlayer.getDisplayName()));
            currentPlayerScore.setScore(1);
        }

        String topic = plugin.getGameManager().getTopic();
        if (topic != null) {
            Score topicScore = obj.getScore(plugin.getLanguageManager().getMessage("scoreboard-topic", "%topic%", topic));
            topicScore.setScore(2);
        }

        Integer remainingQuestions = plugin.getGameManager().getRemainingQuestions();
        Score remainingQuestionsScore = obj.getScore(plugin.getLanguageManager().getMessage("scoreboard-remaining-questions", "%count%", remainingQuestions.toString()));
        remainingQuestionsScore.setScore(3);

        Score emptyLine = obj.getScore(plugin.getLanguageManager().getMessage("scoreboard-empty-line"));
        emptyLine.setScore(4);

        player.setScoreboard(board);
    }
}