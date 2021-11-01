package com.github.nearata.parties.command;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class DisbandCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband"))
                .executesPlayer((player, args) -> {
                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    final PersistentDataContainer data = player.getPersistentDataContainer();
                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        Messages.SENDER_CANNOT_DISBAND.fail();
                    }

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        p.getPersistentDataContainer().remove(plugin.getKey());
                        Messages.PARTY_DISBANDED.send(p);
                    });

                    data.remove(plugin.getKey());
                    team.unregister();

                    Messages.SENDER_DISBANDED.send(player);
                })
                .register();
    }
}
