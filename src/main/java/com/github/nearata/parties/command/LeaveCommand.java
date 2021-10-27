package com.github.nearata.parties.command;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.md_5.bungee.api.ChatColor;

public final class LeaveCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.leave")
                .withArguments(new LiteralArgument("leave"))
                .executesPlayer((player, args) -> {
                    final PersistentDataContainer data = player.getPersistentDataContainer();

                    if (!data.has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessages().get("errors.no_party"));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());

                    data.remove(plugin.getKey());
                    
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);
                    team.removeEntry(player.getName());
                    
                    Util.getOnlineMembers(team.getEntries(), null).forEach(p -> {
                        final String msg = (String) plugin.getMessages().get("info.player_left");
                        p.sendMessage(ChatColor.RED + msg.formatted(player.getName()));
                    });

                    player.sendMessage((String) plugin.getMessages().get("info.party_left"));
                })
                .register();
    }
}
