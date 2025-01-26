package io.dogsbean.twentyquestions.game;

import io.dogsbean.twentyquestions.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TurnDelegationManager {
    private final Main plugin;
    private final GameManager gameManager;
    private final Map<UUID, DelegationRequest> pendingRequests = new HashMap<>();

    public TurnDelegationManager(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void createRequest(Player from, Player to) {
        if (pendingRequests.containsKey(to.getUniqueId())) {
            from.sendMessage(ChatColor.RED + "해당 플레이어에게 이미 다른 양도 요청이 진행 중입니다!");
            return;
        }

        pendingRequests.put(to.getUniqueId(), new DelegationRequest(from, to));
        from.sendMessage(ChatColor.GREEN + to.getName() + "님에게 순서 양도 요청을 보냈습니다.");

        TextComponent message = new TextComponent(ChatColor.GREEN + from.getName() +
                "님이 순서를 양도하려 합니다. ");

        TextComponent accept = new TextComponent(ChatColor.GREEN + "[수락]");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.GREEN + "클릭하여 수락하기").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/순서양도 수락"));

        TextComponent space = new TextComponent(" ");

        TextComponent deny = new TextComponent(ChatColor.RED + "[거절]");
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.RED + "클릭하여 거절하기").create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/순서양도 거절"));

        message.addExtra(accept);
        message.addExtra(space);
        message.addExtra(deny);

        to.spigot().sendMessage(message);
    }

    public void handleResponse(Player player, boolean accept) {
        DelegationRequest request = pendingRequests.remove(player.getUniqueId());
        if (request == null) {
            player.sendMessage(ChatColor.RED + "처리할 순서 양도 요청이 없습니다!");
            return;
        }

        if (accept) {
            if (plugin.getGameManager().getGameState() != GameState.PLAYING ||
                    request.from != plugin.getGameManager().getCurrentPlayer()) {
                player.sendMessage(ChatColor.RED + "순서 양도가 불가능한 상태입니다!");
                return;
            }

            plugin.getGameManager().delegateTurnTo(request.to);
            request.from.sendMessage(ChatColor.GREEN + player.getName() + "님이 순서 양도를 수락했습니다.");
            player.sendMessage(ChatColor.GREEN + "순서 양도를 수락했습니다.");
        } else {
            request.from.sendMessage(ChatColor.RED + player.getName() + "님이 순서 양도를 거절했습니다.");
            player.sendMessage(ChatColor.RED + "순서 양도를 거절했습니다.");
        }
    }

    private static class DelegationRequest {
        final Player from;
        final Player to;

        DelegationRequest(Player from, Player to) {
            this.from = from;
            this.to = to;
        }
    }
}
