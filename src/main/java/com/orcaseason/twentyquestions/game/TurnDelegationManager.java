package com.orcaseason.twentyquestions.game;

import com.orcaseason.twentyquestions.Main;
import com.orcaseason.twentyquestions.lang.LanguageManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TurnDelegationManager {
    private final Main plugin;
    private final GameManager gameManager;
    private final LanguageManager languageManager;
    private final Map<UUID, DelegationRequest> pendingRequests = new HashMap<>();

    public TurnDelegationManager(Main plugin, GameManager gameManager, LanguageManager languageManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.languageManager = languageManager;
    }

    public void createRequest(Player from, Player to) {
        if (pendingRequests.containsKey(to.getUniqueId())) {
            from.sendMessage(languageManager.getMessage("delegation-request-exists"));
            return;
        }

        pendingRequests.put(to.getUniqueId(), new DelegationRequest(from, to));
        from.sendMessage(languageManager.getMessage("delegation-request-sent", "%player%", to.getName()));

        TextComponent message = new TextComponent(languageManager.getMessage("delegation-prompt", "%from%", from.getName()));

        TextComponent accept = new TextComponent(languageManager.getMessage("delegation-accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(languageManager.getMessage("delegation-accept-hover")).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/순서양도 accept"));

        TextComponent space = new TextComponent(" ");

        TextComponent deny = new TextComponent(languageManager.getMessage("delegation-deny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(languageManager.getMessage("delegation-deny-hover")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/순서양도 deny"));

        message.addExtra(accept);
        message.addExtra(space);
        message.addExtra(deny);

        to.spigot().sendMessage(message);
    }

    public void handleResponse(Player player, boolean accept) {
        DelegationRequest request = pendingRequests.remove(player.getUniqueId());
        if (request == null) {
            player.sendMessage(languageManager.getMessage("no-delegation-request"));
            return;
        }

        if (accept) {
            if (plugin.getGameManager().getGameState() != GameState.PLAYING ||
                    request.from != plugin.getGameManager().getCurrentPlayer()) {
                player.sendMessage(languageManager.getMessage("delegation-invalid-state"));
                return;
            }

            plugin.getGameManager().delegateTurnTo(request.to);
            request.from.sendMessage(languageManager.getMessage("delegation-accepted-from", "%player%", player.getName()));
            player.sendMessage(languageManager.getMessage("delegation-accepted-to"));
        } else {
            request.from.sendMessage(languageManager.getMessage("delegation-denied-from", "%player%", player.getName()));
            player.sendMessage(languageManager.getMessage("delegation-denied-to"));
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