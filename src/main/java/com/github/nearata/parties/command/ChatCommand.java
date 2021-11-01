package com.github.nearata.parties.command;

import java.util.Set;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ChatCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.chat")
                .withArguments(new LiteralArgument("chat"))
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    final String message = (String) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    final String rankname = plugin.getMessage(Util.getRank(player).get());

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (Set.of(PartyRank.LEADER, PartyRank.MOD).contains(Util.getRank(player)))
                        {
                            Messages.PARTY_CHAT_MESSAGE_HAS_RANK.send(p, rankname, player.getName(), message);
                        }
                        else
                        {
                            Messages.PARTY_CHAT_MESSAGE.send(p, player.getName(), message);
                        }
                    });
                })
                .register();
    }
}
