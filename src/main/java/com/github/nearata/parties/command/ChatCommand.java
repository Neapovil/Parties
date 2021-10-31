package com.github.nearata.parties.command;

import java.util.Set;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
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
                        CommandAPI.fail(plugin.getMessage(Messages.NO_PARTY.get()));
                    }

                    final String rankname = plugin.getMessage(Util.getRank(player).get());
                    final String msg = Set.of(PartyRank.LEADER, PartyRank.MOD).contains(Util.getRank(player))
                            ? plugin.getMessage(Messages.PARTY_CHAT_MESSAGE_HAS_RANK.get()).formatted(rankname, player.getName(), message)
                            : plugin.getMessage(Messages.PARTY_CHAT_MESSAGE.get()).formatted(player.getName(), message);

                    Util.getOnlineMembers(player).forEach(p -> {
                        p.sendMessage(msg);
                    });
                })
                .register();
    }
}
