package com.github.nearata.parties.command;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
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
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
                    }

                    final PersistentDataContainer data = player.getPersistentDataContainer();
                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.CANNOT_DISBAND_NOT_LEADER.get()));
                    }

                    final String msg = plugin.getMessage(MessageInfo.PARTY_DISBANDED_BY.get());
                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        p.getPersistentDataContainer().remove(plugin.getKey());
                        p.sendMessage(msg);
                    });

                    data.remove(plugin.getKey());
                    team.unregister();

                    player.sendMessage(plugin.getMessage(MessageInfo.PARTY_DISBANDED.get()));
                })
                .register();
    }
}
