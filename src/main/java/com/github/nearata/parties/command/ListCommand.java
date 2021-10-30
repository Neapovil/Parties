package com.github.nearata.parties.command;

import java.util.List;

import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ListCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.list")
                .withArguments(new LiteralArgument("list"))
                .executesPlayer((player, args) -> {
                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
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

                    player.sendMessage("Party Leader: " + leader);
                    player.sendMessage("Party Mod: " + String.join(", ", mods));
                    player.sendMessage("Party Members: " + String.join(", ", members));
                })
                .register();
    }
}
