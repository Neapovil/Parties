package com.github.nearata.parties.command;

import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

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
                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();
                    if (Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_LEAVE_LEADER.get()));
                    }

                    final String msg = plugin.getMessage(Messages.PARTY_PLAYER_LEFT.get()).formatted(player.getName());
                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        p.sendMessage(msg);
                    });

                    player.getPersistentDataContainer().remove(plugin.getKey());
                    team.removeEntry(player.getName());

                    player.sendMessage(plugin.getMessage(Messages.SENDER_PARTY_LEFT.get()));
                })
                .register();
    }
}
