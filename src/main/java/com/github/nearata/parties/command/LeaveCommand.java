package com.github.nearata.parties.command;

import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.md_5.bungee.api.ChatColor;

public final class LeaveCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.leave")
                .withArguments(new LiteralArgument("leave"))
                .executesPlayer((player, args) -> {
                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();
                    if (team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.CANNOT_LEAVE_LEADER.get()));
                    }

                    final String msg = plugin.getMessage(MessageInfo.PLAYER_LEFT.get()).formatted(player.getName());
                    Util.getOnlineMembers(player, true).forEach(p -> {
                        p.sendMessage(ChatColor.RED + msg);
                    });

                    player.getPersistentDataContainer().remove(plugin.getKey());
                    team.removeEntry(player.getName());

                    player.sendMessage(plugin.getMessage(MessageInfo.PARTY_LEFT.get()));
                })
                .register();
    }
}
