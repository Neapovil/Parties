package com.github.neapovil.parties.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.neapovil.parties.Parties;

public final class Listener implements org.bukkit.event.Listener
{
    private final Parties plugin = Parties.instance();

    @EventHandler
    private void playerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.findMember(player).isEmpty())
        {
            player.getPersistentDataContainer().remove(plugin.partyIdKey);
        }
    }
}
