package io.dogsbean.twentyquestions;

import io.dogsbean.twentyquestions.commands.GameCommandHandler;
import io.dogsbean.twentyquestions.game.GameManager;
import io.dogsbean.twentyquestions.game.GameState;
import io.dogsbean.twentyquestions.game.TurnDelegationGUI;
import io.dogsbean.twentyquestions.game.TurnDelegationManager;
import io.dogsbean.twentyquestions.lang.LanguageManager;
import io.dogsbean.twentyquestions.listener.BasicPreventionListener;
import io.dogsbean.twentyquestions.scoreboard.ScoreboardManager;
import lombok.Getter;
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

@Getter
public class Main extends JavaPlugin {
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private TurnDelegationManager turnDelegationManager;
    private LanguageManager languageManager;
    private GameCommandHandler gameCommandHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        String language = config.getString("language", "ko");

        languageManager = new LanguageManager(this, language);
        scoreboardManager = new ScoreboardManager(this);
        gameManager = new GameManager(this, scoreboardManager, languageManager);
        turnDelegationManager = new TurnDelegationManager(this, gameManager, languageManager);
        gameCommandHandler = new GameCommandHandler(this, gameManager, languageManager);

        getLogger().info("20 Questions plugin has been enabled!");

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "순서 양도하기")) {
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
        getLogger().info("20 Questions plugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "이 명령어는 플레이어만 사용할 수 있습니다!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equals("순서양도")) {
            if (args.length == 0) {
                if (gameManager.getGameState() != GameState.PLAYING || player != gameManager.getCurrentPlayer()) {
                    player.sendMessage(ChatColor.RED + "지금은 순서를 양도할 수 없습니다!");
                    return true;
                }
                new TurnDelegationGUI(this, player, gameManager.getPlayers(), languageManager).open();
                return true;
            } else if (args.length == 1) {
                if (args[0].equals("수락") || args[0].equals("거절")) {
                    turnDelegationManager.handleResponse(player, args[0].equals("수락"));
                    return true;
                }
            }
            return false;
        }

        return gameCommandHandler.handleCommand(player, command.getName(), args);
    }
}