package com.github.nearata.parties.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.util.Util;

public final class Listener implements org.bukkit.event.Listener
{
    private final Parties plugin = Parties.getInstance();

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        if (Util.getParty(player).isEmpty())
        {
            player.getPersistentDataContainer().remove(plugin.getKey());
        }
    }
}
