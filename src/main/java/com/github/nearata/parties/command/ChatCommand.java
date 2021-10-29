package com.github.nearata.parties.command;

import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.util.Util;

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
                .withPermission("party.command.chat")
                .withArguments(new LiteralArgument("chat"))
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();
                    final String msg = plugin.getMessage("info.party_chat").formatted(player.getName(), (String) args[0]);

                    Util.getOnlineMembers(team.getEntries(), null).forEach(p -> {
                        p.sendMessage(msg);
                    });
                })
                .register();
    }
}
