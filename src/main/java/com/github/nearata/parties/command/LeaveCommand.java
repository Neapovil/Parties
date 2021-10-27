package com.github.nearata.parties.command;

import org.bukkit.persistence.PersistentDataContainer;

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
                        CommandAPI.fail(plugin.getMessages().get("errors.no_party"));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());

                    data.remove(plugin.getKey());
                    plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid).removeEntry(player.getName());

                    player.sendMessage((String) plugin.getMessages().get("info.party_left"));
                })
                .register();
    }
}
