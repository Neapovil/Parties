package com.github.nearata.parties.command;

import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

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
                    if (!player.getPersistentDataContainer().has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessagesConfig().get("errors.no_party"));
                    }

                    final Team team = plugin.getServer()
                            .getScoreboardManager()
                            .getMainScoreboard()
                            .getTeam(player.getPersistentDataContainer().get(plugin.getKey(), plugin.getKeyType()));

                    player.sendMessage("Party Members: " + String.join(", ", team.getEntries()));
                })
                .register();
    }
}
