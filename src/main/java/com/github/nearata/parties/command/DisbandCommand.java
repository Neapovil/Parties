package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.md_5.bungee.api.ChatColor;

public final class DisbandCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    final PersistentDataContainer data = player.getPersistentDataContainer();

                    if (!data.has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessagesConfig().get("errors.no_party"));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());

                    data.remove(plugin.getKey());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    for (String username : team.getEntries())
                    {
                        final Player partyplayer = plugin.getServer().getPlayer(username);

                        if (partyplayer == null)
                        {
                            continue;
                        }

                        if (partyplayer.getUniqueId().equals(uuid))
                        {
                            continue;
                        }

                        partyplayer.getPersistentDataContainer().remove(plugin.getKey());
                        partyplayer.sendMessage(ChatColor.RED + (String) plugin.getMessagesConfig().get("info.party_disbanded_by"));
                    }

                    team.unregister();
                    player.sendMessage(ChatColor.RED + (String) plugin.getMessagesConfig().get("info.party_disbanded"));
                })
                .register();
    }
}
