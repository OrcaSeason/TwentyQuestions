package com.orcaseason.twentyquestions.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private final JavaPlugin plugin;
    private final String language;
    private FileConfiguration messages;

    private static final Map<String, Map<String, String>> DEFAULT_MESSAGES = new HashMap<>();

    static {
        Map<String, String> koMessages = new HashMap<>();
        koMessages.put("delegation-accept", "&a[수락]");
        koMessages.put("error-no-players", "&c[Error 1]: 플레이어를 찾을 수 없습니다.");
        koMessages.put("cannot-ask-question", "지금은 질문을 할 수 없습니다!");
        koMessages.put("cannot-reject-hint", "지금은 힌트 제공을 거절할 수 없습니다!");
        koMessages.put("cannot-reveal-answer", "지금은 정답을 공개할 수 없습니다!");
        koMessages.put("plugin-enabled", "스무고개 플러그인이 활성화되었습니다!");
        koMessages.put("player-only-command", "이 명령어는 플레이어만 사용할 수 있습니다!");
        koMessages.put("question-broadcast", "&f◾ &a%player%의 질문: \n&e&l'%question%'");
        koMessages.put("cannot-answer", "지금은 답변을 할 수 없습니다!");
        koMessages.put("delegation-accepted-to", "&a순서 양도를 수락했습니다.");
        koMessages.put("delegation-request-sent", "&a%player%님에게 순서 양도 요청을 보냈습니다.");
        koMessages.put("scoreboard-questioner", "&f출제자: &6%questioner%");
        koMessages.put("delegation-prompt", "&a%from%님이 순서를 양도하려 합니다. ");
        koMessages.put("hint-yes-hover", "힌트를 제공하려면 클릭하세요.");
        koMessages.put("timeout-message", "&c%player%님이 1분 동안 아무 행동도 하지 않았습니다. 순서가 넘어갑니다.");
        koMessages.put("questioner-win", "&a출제자 %questioner%의 승리!");
        koMessages.put("game-already-in-progress", "게임이 이미 진행 중입니다!");
        koMessages.put("answer-broadcast", "&f◾ 출제자의 답변: &a%answer%");
        koMessages.put("cannot-delegate-now", "지금은 순서를 양도할 수 없습니다!");
        koMessages.put("game-starting-soon", "&a게임이 곧 시작됩니다!");
        koMessages.put("remaining-questions", "&e남은 기회: %count%");
        koMessages.put("plugin-disabled", "스무고개 플러그인이 비활성화되었습니다!");
        koMessages.put("delegation-request-exists", "&c해당 플레이어에게 이미 다른 양도 요청이 진행 중입니다!");
        koMessages.put("delegation-denied-to", "&c순서 양도를 거절했습니다.");
        koMessages.put("countdown", "&e%count%");
        koMessages.put("error-no-available-players", "&c[Error 2]: 이전 플레이어를 제외한 플레이어를 찾을 수 없습니다.");
        koMessages.put("no-delegation-request", "&c처리할 순서 양도 요청이 없습니다!");
        koMessages.put("scoreboard-current-player", "&f질문자: &6%player%");
        koMessages.put("hint-yes", "&a[제공]");
        koMessages.put("delegation-denied-from", "&c%player%님이 순서 양도를 거절했습니다.");
        koMessages.put("turn-delegation-gui-title", "&2순서 양도하기");
        koMessages.put("hint-no", "&c[거절]");
        koMessages.put("game-started", "&a&l스무고개가 시작되었습니다!");
        koMessages.put("answer-no", "&c[X]");
        koMessages.put("hint-no-hover", "힌트 제공을 거절하려면 클릭하세요.");
        koMessages.put("correct-answer", "&a%player%님이 정답을 맞추셨습니다!");
        koMessages.put("hint-broadcast", "&f◾ 힌트: &a%hint%");
        koMessages.put("cannot-set-topic", "지금은 주제를 설정할 수 없습니다!");
        koMessages.put("game-started-playing", "&a&l게임이 시작되었습니다!");
        koMessages.put("answer-yes", "&a[O]");
        koMessages.put("scoreboard-remaining-questions", "&f남은 기회: &6%count%");
        koMessages.put("cannot-set-answer", "지금은 정답을 설정할 수 없습니다!");
        koMessages.put("answer-yes-hover", "질문이 정답과 관련이 있을 경우 클릭하세요.");
        koMessages.put("cannot-deduct-chance", "지금은 기회를 차감할 수 없습니다!");
        koMessages.put("topic-announce", "&f◾ 주제: &a&l%topic%");
        koMessages.put("questioner-announce", "&f◾ 출제자: &a%questioner%");
        koMessages.put("topic-set", "&a&l✔ 주제가 설정되었습니다!");
        koMessages.put("not-enough-players", "게임을 시작하기 위해서는 최소 2명의 플레이어가 필요합니다!");
        koMessages.put("scoreboard-topic", "&f주제: &6%topic%");
        koMessages.put("wrong-answer", "&c%player%님의 정답이 틀렸습니다!");
        koMessages.put("answer-reveal", "&6정답은 '%answer%'였습니다!");
        koMessages.put("set-answer-prompt", "&e'/정답 설정 <정답>'으로 정답을 설정해주세요.");
        koMessages.put("delegation-deny-hover", "클릭하여 거절하기");
        koMessages.put("cannot-guess-answer", "지금은 정답을 맞출 수 없습니다!");
        koMessages.put("delegation-invalid-state", "&c순서 양도가 불가능한 상태입니다!");
        koMessages.put("answer-prompt", "&6답변하기: ");
        koMessages.put("hint-rejected", "&c&l출제자가 힌트 제공을 거절했습니다!");
        koMessages.put("hint-prompt", "&6힌트를 제공하시겠습니까? ");
        koMessages.put("current-player-announce", "&f◾ 현재 차례: &a%player%");
        koMessages.put("delegation-deny", "&c[거절]");
        koMessages.put("delegation-accepted-from", "&a%player%님이 순서 양도를 수락했습니다.");
        koMessages.put("set-topic-prompt", "&e'/주제 설정 <주제>'로 주제를 설정해주세요.");
        koMessages.put("delegation-accept-hover", "클릭하여 수락하기");
        koMessages.put("players-win", "&a참가자들의 승리!");
        koMessages.put("scoreboard-empty-line", "&f");
        koMessages.put("cannot-provide-hint", "지금은 힌트를 제공할 수 없습니다!");
        koMessages.put("answer-no-hover", "질문이 정답과 관련이 없을 경우 클릭하세요.");
        DEFAULT_MESSAGES.put("ko", koMessages);

        Map<String, String> enMessages = new HashMap<>();
        enMessages.put("delegation-accept", "&a[Accept]");
        enMessages.put("error-no-players", "&c[Error 1]: No players found.");
        enMessages.put("cannot-ask-question", "You cannot ask a question now!");
        enMessages.put("cannot-reject-hint", "You cannot decline to provide a hint now!");
        enMessages.put("cannot-reveal-answer", "You cannot reveal the answer now!");
        enMessages.put("plugin-enabled", "Twenty Questions plugin has been enabled!");
        enMessages.put("player-only-command", "This command can only be used by players!");
        enMessages.put("question-broadcast", "&f◾ &a%player%'s question: \n&e&l'%question%'");
        enMessages.put("cannot-answer", "You cannot respond now!");
        enMessages.put("delegation-accepted-to", "&aYou accepted the turn delegation.");
        enMessages.put("delegation-request-sent", "&aSent a turn delegation request to %player%.");
        enMessages.put("scoreboard-questioner", "&fQuestioner: &6%questioner%");
        enMessages.put("delegation-prompt", "&a%from% wants to delegate their turn to you. ");
        enMessages.put("hint-yes-hover", "Click to provide a hint.");
        enMessages.put("timeout-message", "&c%player% has been inactive for 1 minute. Passing the turn.");
        enMessages.put("questioner-win", "&aQuestioner %questioner% wins!");
        enMessages.put("game-already-in-progress", "A game is already in progress!");
        enMessages.put("answer-broadcast", "&f◾ Questioner's response: &a%answer%");
        enMessages.put("cannot-delegate-now", "You cannot delegate your turn now!");
        enMessages.put("game-starting-soon", "&aThe game will start soon!");
        enMessages.put("remaining-questions", "&eQuestions remaining: %count%");
        enMessages.put("plugin-disabled", "Twenty Questions plugin has been disabled!");
        enMessages.put("delegation-request-exists", "&cThere is already a pending delegation request for this player!");
        enMessages.put("delegation-denied-to", "&cYou denied the turn delegation.");
        enMessages.put("countdown", "&e%count%");
        enMessages.put("error-no-available-players", "&c[Error 2]: No players available excluding the previous player.");
        enMessages.put("no-delegation-request", "&cThere is no pending delegation request to process!");
        enMessages.put("scoreboard-current-player", "&fCurrent Player: &6%player%");
        enMessages.put("hint-yes", "&a[Provide]");
        enMessages.put("delegation denied-from", "&c%player% denied the turn delegation.");
        enMessages.put("turn-delegation-gui-title", "&2Delegate Turn");
        enMessages.put("hint-no", "&c[Deny]");
        enMessages.put("game-started", "&a&lTwenty Questions has started!");
        enMessages.put("answer-no", "&c[No]");
        enMessages.put("hint-no-hover", "Click to deny providing a hint.");
        enMessages.put("correct-answer", "&a%player% guessed the answer correctly!");
        enMessages.put("hint-broadcast", "&f◾ Hint: &a%hint%");
        enMessages.put("cannot-set-topic", "You cannot set the topic now!");
        enMessages.put("game-started-playing", "&a&lThe game has started!");
        enMessages.put("answer-yes", "&a[Yes]");
        enMessages.put("scoreboard-remaining-questions", "&fQuestions Left: &6%count%");
        enMessages.put("cannot-set-answer", "You cannot set the answer now!");
        enMessages.put("answer-yes-hover", "Click if the question is relevant to the answer.");
        enMessages.put("cannot-deduct-chance", "You cannot deduct a question now!");
        enMessages.put("topic-announce", "&f◾ Topic: &a&l%topic%");
        enMessages.put("questioner-announce", "&f◾ Questioner: &a%questioner%");
        enMessages.put("topic-set", "&a&l✔ Topic has been set!");
        enMessages.put("not-enough-players", "At least 2 players are required to start the game!");
        enMessages.put("scoreboard-topic", "&fTopic: &6%topic%");
        enMessages.put("wrong-answer", "&c%player%'s guess was incorrect!");
        enMessages.put("answer-reveal", "&6The answer was '%answer%'!");
        enMessages.put("set-answer-prompt", "&ePlease set the answer using '/answer set <answer>'.");
        enMessages.put("delegation-deny-hover", "Click to deny");
        enMessages.put("cannot-guess-answer", "You cannot guess the answer now!");
        enMessages.put("delegation-invalid-state", "&cTurn delegation is not possible at this time!");
        enMessages.put("answer-prompt", "&6Respond: ");
        enMessages.put("hint-rejected", "&c&lThe questioner declined to provide a hint!");
        enMessages.put("hint-prompt", "&6Would you like to provide a hint? ");
        enMessages.put("current-player-announce", "&f◾ Current turn: &a%player%");
        enMessages.put("delegation-deny", "&c[Deny]");
        enMessages.put("delegation-accepted-from", "&a%player% accepted the turn delegation.");
        enMessages.put("set-topic-prompt", "&ePlease set the topic using '/topic set <topic>'.");
        enMessages.put("delegation-accept-hover", "Click to accept");
        enMessages.put("players-win", "&aThe players win!");
        enMessages.put("scoreboard-empty-line", "&f");
        enMessages.put("cannot-provide-hint", "You cannot provide a hint now!");
        enMessages.put("answer-no-hover", "Click if the question is not relevant to the answer.");
        DEFAULT_MESSAGES.put("en", enMessages);
    }

    public LanguageManager(JavaPlugin plugin, String language) {
        this.plugin = plugin;
        this.language = language != null ? language.toLowerCase() : "ko";
    }

    public void loadLanguage() {
        String effectiveLanguage = language;
        if (!DEFAULT_MESSAGES.containsKey(language)) {
            plugin.getLogger().warning("Invalid language '" + language + "'. Falling back to 'ko'.");
            effectiveLanguage = "ko";
        }
        plugin.getLogger().info("Loading language: " + effectiveLanguage);

        File dataFolder = plugin.getDataFolder();

        String[] languages = {"ko", "en"};
        for (String lang : languages) {
            saveLanguageFile(lang);
        }

        String fileName = "messages_" + effectiveLanguage + ".yml";
        File file = new File(dataFolder, fileName);
        plugin.getLogger().info("Attempting to load language file: " + file.getPath());

        messages = null;
        try {
            if (!file.exists()) {
                saveLanguageFile(effectiveLanguage);
            }
            messages = YamlConfiguration.loadConfiguration(file);
            if (messages.getKeys(false).isEmpty()) {
                messages = getDefaultMessages(effectiveLanguage);
            }
        } catch (Exception e) {
            messages = getDefaultMessages(effectiveLanguage);
        }

        if (messages == null) {
            messages = getDefaultMessages(effectiveLanguage);
        }

        String[] criticalKeys = {"plugin-enabled", "game-started", "questioner-announce", "set-topic-prompt"};
        for (String key : criticalKeys) {
            if (messages.contains(key)) {
                plugin.getLogger().info("Key '" + key + "' found: " + messages.getString(key));
            } else {
                plugin.getLogger().warning("Key '" + key + "' missing in loaded messages.");
            }
        }

        FileConfiguration defaultMessages = getDefaultMessages(effectiveLanguage);
        int missingKeys = 0;
        for (String key : defaultMessages.getKeys(true)) {
            if (!messages.contains(key)) {
                messages.set(key, defaultMessages.get(key));
                missingKeys++;
            }
        }
        if (missingKeys > 0) {
            try {
                messages.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save " + fileName + ": " + e.getMessage());
            }
        }
    }

    private void saveLanguageFile(String lang) {
        String fileName = "messages_" + lang + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                InputStream resource = plugin.getResource(fileName);
                if (resource != null) {
                    plugin.saveResource(fileName, false);
                } else {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    if (file.createNewFile()) {
                        FileConfiguration config = new YamlConfiguration();
                        Map<String, String> defaults = DEFAULT_MESSAGES.getOrDefault(lang, new HashMap<>());
                        for (Map.Entry<String, String> entry : defaults.entrySet()) {
                            config.set(entry.getKey(), entry.getValue());
                        }
                        config.save(file);
                    } else {
                        plugin.getLogger().warning("Failed to create file: " + fileName);
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create or save " + fileName + ": " + e.getMessage());
            }
        } else {
            plugin.getLogger().info("Language file " + fileName + " already exists.");
        }

        if (file.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (config.getKeys(false).isEmpty()) {
                    Map<String, String> defaults = DEFAULT_MESSAGES.getOrDefault(lang, new HashMap<>());
                    for (Map.Entry<String, String> entry : defaults.entrySet()) {
                        config.set(entry.getKey(), entry.getValue());
                    }
                    config.save(file);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to verify contents of " + fileName + ": " + e.getMessage());
            }
        }
    }

    private FileConfiguration getDefaultMessages(String lang) {
        String fileName = "messages_" + lang + ".yml";
        InputStream stream = plugin.getResource(fileName);
        if (stream != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (config.getKeys(false).isEmpty()) {
                plugin.getLogger().warning("Resource " + fileName + " is empty. Using hardcoded defaults.");
            } else {
                return config;
            }
        }
        plugin.getLogger().info("No resource found for " + fileName + ". Using hardcoded defaults.");
        FileConfiguration config = new YamlConfiguration();
        Map<String, String> defaults = DEFAULT_MESSAGES.getOrDefault(lang, new HashMap<>());
        for (Map.Entry<String, String> entry : defaults.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        return config;
    }

    public String getMessage(String key) {
        String message = messages.getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key, String... placeholders) {
        String message = getMessage(key);
        if (message.startsWith(ChatColor.RED + "Error:")) {
            return message;
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message;
    }

    public String getLanguage() {
        return language;
    }
}