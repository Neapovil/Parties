package com.github.nearata.parties.command;

import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

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
                        Messages.NO_PARTY.fail();
                    }

                    final Team team = Util.getParty(player).get();
                    if (Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        Messages.SENDER_CANNOT_LEAVE_LEADER.fail();
                    }

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        Messages.PARTY_PLAYER_LEFT.send(p, player.getName());
                    });

                    player.getPersistentDataContainer().remove(plugin.getKey());
                    team.removeEntry(player.getName());

                    Messages.SENDER_PARTY_LEFT.send(player);
                })
                .register();
    }
}
