package com.github.neapovil.parties.command;

import java.util.List;

import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.messages.Messages;
import com.github.neapovil.parties.util.Util;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ListCommand
{
    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.list")
                .withArguments(new LiteralArgument("list"))
                .executesPlayer((player, args) -> {
                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    final Team team = Util.getParty(player).get();

                    final List<String> members = team.getEntries()
                            .stream()
                            .filter(s -> !s.startsWith("leader-"))
                            .filter(s -> !s.startsWith("mod-"))
                            .toList();

                    final String leader = team.getEntries()
                            .stream()
                            .filter(s -> s.startsWith("leader-"))
                            .map(s -> s.replace("leader-", ""))
                            .findFirst()
                            .get();

                    final List<String> mods = team.getEntries()
                            .stream()
                            .filter(e -> e.startsWith("mod-"))
                            .map(s -> s.replace("mod-", ""))
                            .toList();

                    Messages.SENDER_PARTY_LIST_LEADER.send(player, leader);
                    Messages.SENDER_PARTY_LIST_MODS.send(player, String.join(", ", mods));
                    Messages.SENDER_PARTY_LIST_MEMBERS.send(player, String.join(", ", members));
                })
                .register();
    }
}
