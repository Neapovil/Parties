package com.github.nearata.parties.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.github.nearata.parties.Parties;

import net.md_5.bungee.api.ChatColor;

public final class PartiesListener implements Listener
{
    private final Parties plugin = Parties.getInstance();

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final PersistentDataContainer data = player.getPersistentDataContainer();
        final NamespacedKey key = new NamespacedKey(plugin, "party");

        if (!data.has(key, PersistentDataType.STRING))
        {
            return;
        }

        final String partyid = data.get(key, PersistentDataType.STRING);

        if (plugin.getPartiesConfig().get("parties." + partyid) != null)
        {
            return;
        }

        player.getScoreboard().getTeam(partyid).unregister();
        data.remove(key);

        player.sendMessage(ChatColor.RED + (String) plugin.getMessagesConfig().get("info.party_disbanded_by"));
    }
}
