package com.github.neapovil.parties.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.util.Util;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ListCommand implements ICommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.list")
                .withArguments(new LiteralArgument("list").withRequirement(sender -> {
                    return Util.getParty((Player) sender).isPresent();
                }))
                .executesPlayer((player, args) -> {
                    final Team team = Util.getParty(player).get();

                    final List<String> members = team.getEntries()
                            .stream()
                            .filter(i -> !i.startsWith("leader-"))
                            .filter(i -> !i.startsWith("mod-"))
                            .toList();

                    final String leader = team.getEntries()
                            .stream()
                            .filter(i -> i.startsWith("leader-"))
                            .map(i -> i.replace("leader-", ""))
                            .findFirst()
                            .get();

                    final List<String> mods = team.getEntries()
                            .stream()
                            .filter(i -> i.startsWith("mod-"))
                            .map(i -> i.replace("mod-", ""))
                            .toList();

                    player.sendMessage("Party Leader: %s".formatted(leader));
                    player.sendMessage("Party Moderators: %s".formatted(String.join(", ", mods)));
                    player.sendMessage("Party Members: %s".formatted(String.join(", ", members)));
                })
                .register();
    }
}
