package com.github.nearata.parties.command;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class KickCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("party.command.kick")
                .withArguments(new LiteralArgument("kick"))
                .withArguments(new StringArgument("player").replaceSuggestions(info -> {
                    return Arrays.asList(plugin.getServer().getOfflinePlayers()).stream().map(p -> p.getName()).toArray(String[]::new);
                }))
                .executesPlayer((player, args) -> {
                    final String playername = (String) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();

                    if (!team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PERMISSIONS.get()));
                    }

                    if (!team.getEntries().contains(playername))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.CANNOT_KICK_NOT_IN_PARTY.get()));
                    }

                    team.removeEntry(playername);

                    final Player player1 = plugin.getServer().getPlayer(playername);

                    if (player1 != null)
                    {
                        player1.getPersistentDataContainer().remove(plugin.getKey());
                        player1.sendMessage(MessageInfo.PARTY_KICKED.get());
                    }

                    final String msg = MessageInfo.PLAYER_KICKED.get().formatted(player1.getName());
                    Util.getOnlineMembers(team.getEntries(), null).forEach(p -> {
                        p.sendMessage(msg);
                    });

                    player.sendMessage(plugin.getMessage(MessageInfo.YOU_KICKED.get()));
                })
                .register();
    }
}
