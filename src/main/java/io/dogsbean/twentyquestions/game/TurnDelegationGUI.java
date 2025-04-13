package io.dogsbean.twentyquestions.game;

import io.dogsbean.twentyquestions.Main;
import io.dogsbean.twentyquestions.lang.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class TurnDelegationGUI {
    private final Main plugin;
    private final Player sender;
    private final List<Player> players;
    private final LanguageManager languageManager;

    public TurnDelegationGUI(Main plugin, Player sender, List<Player> players, LanguageManager languageManager) {
        this.plugin = plugin;
        this.sender = sender;
        this.players = players;
        this.languageManager = languageManager;
    }

    public void open() {
        int size = ((players.size() - 1) / 9 + 1) * 9;
        Inventory inv = Bukkit.createInventory(null, size, languageManager.getMessage("turn-delegation-gui-title"));

        for (Player player : players) {
            if (player != sender && player != plugin.getGameManager().getQuestioner()) {
                ItemStack head = new ItemStack(Material.SKULL_ITEM);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + player.getName());
                head.setItemMeta(meta);
                inv.addItem(head);
            }
        }

        sender.openInventory(inv);
    }
}