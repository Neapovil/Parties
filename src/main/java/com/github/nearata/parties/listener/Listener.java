package com.github.nearata.parties.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

public final class Listener implements org.bukkit.event.Listener
{
    private final Parties plugin = Parties.getInstance();

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final PersistentDataContainer data = player.getPersistentDataContainer();

        if (!data.has(plugin.getKey(), plugin.getKeyType()))
        {
            return;
        }

        final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
        final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

        if (team == null)
        {
            data.remove(plugin.getKey());
        }
    }
}
