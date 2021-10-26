package com.github.nearata.parties.command;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

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
                        CommandAPI.fail(plugin.getMessagesConfig().get("errors.no_party"));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    data.remove(plugin.getKey());
                    team.removeEntry(player.getName());

                    player.sendMessage((String) plugin.getMessagesConfig().get("info.party_left"));
                })
                .register();
    }
}
