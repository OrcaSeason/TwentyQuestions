package com.orcaseason.twentyquestions;

import com.orcaseason.twentyquestions.commands.GameCommandHandler;
import com.orcaseason.twentyquestions.game.GameManager;
import com.orcaseason.twentyquestions.game.GameState;
import com.orcaseason.twentyquestions.game.TurnDelegationGUI;
import com.orcaseason.twentyquestions.game.TurnDelegationManager;
import com.orcaseason.twentyquestions.lang.LanguageManager;
import com.orcaseason.twentyquestions.listener.BasicPreventionListener;
import com.orcaseason.twentyquestions.scoreboard.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private TurnDelegationManager turnDelegationManager;
    private GameCommandHandler gameCommandHandler;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        if (!config.isSet("language")) {
            config.set("language", "ko");
            try {
                saveConfig();
                getLogger().info("Initialized config.yml with default language: ko");
            } catch (Exception e) {
                getLogger().warning("Failed to save config.yml: " + e.getMessage());
            }
        }
        String language = config.getString("language", "ko");
        if (language == null || (!language.equalsIgnoreCase("ko") && !language.equalsIgnoreCase("en"))) {
            getLogger().warning("Invalid language '" + language + "' in config.yml. Defaulting to 'ko'.");
            language = "ko";
            config.set("language", "ko");
            try {
                saveConfig();
                getLogger().info("Updated config.yml with language: ko");
            } catch (Exception e) {
                getLogger().warning("Failed to update config.yml: " + e.getMessage());
            }
        }
        getLogger().info("Selected language: " + language);

        languageManager = new LanguageManager(this, language);
        try {
            languageManager.loadLanguage();
            getLogger().info("LanguageManager initialized successfully");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize LanguageManager: " + e.getMessage());
            languageManager = new LanguageManager(this, "ko");
            languageManager.loadLanguage();
        }

        scoreboardManager = new ScoreboardManager(this);
        gameManager = new GameManager(this, scoreboardManager, languageManager);
        turnDelegationManager = new TurnDelegationManager(this, gameManager, languageManager);
        gameCommandHandler = new GameCommandHandler(this, gameManager, languageManager);

        getLogger().info(languageManager.getMessage("plugin-enabled"));

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                String title = languageManager.getMessage("turn-delegation-gui-title");
                if (event.getView().getTitle().equals(title)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.SKULL_ITEM) {
                        return;
                    }

                    Player target = getServer().getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
                    if (target != null) {
                        turnDelegationManager.createRequest((Player) event.getWhoClicked(), target);
                    }

                    event.getWhoClicked().closeInventory();
                }
            }
        }, this);

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info(languageManager.getMessage("plugin-disabled"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(languageManager.getMessage("player-only-command"));
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("순서양도") || command.getName().equalsIgnoreCase("delegate")) {
            if (args.length == 0) {
                if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getCurrentPlayer()) {
                    player.sendMessage(languageManager.getMessage("cannot-delegate-now"));
                    return true;
                }
                new TurnDelegationGUI(this, player, gameManager.getPlayers(), languageManager).open();
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("수락") || args[0].equalsIgnoreCase("accept")) {
                    turnDelegationManager.handleResponse(player, true);
                    return true;
                } else if (args[0].equalsIgnoreCase("거절") || args[0].equalsIgnoreCase("deny")) {
                    turnDelegationManager.handleResponse(player, false);
                    return true;
                }
            }
            return false;
        }

        return gameCommandHandler.handleCommand(player, command.getName(), args);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}