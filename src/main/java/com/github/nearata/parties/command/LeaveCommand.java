package com.github.nearata.parties.command;

import org.bukkit.persistence.PersistentDataContainer;
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
                    final PersistentDataContainer data = player.getPersistentDataContainer();

                    if (!data.has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessages().get(MessageError.NO_PARTY.get()));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    if (team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail((String) plugin.getMessages().get(MessageError.CANNOT_LEAVE_LEADER.get()));
                    }

                    data.remove(plugin.getKey());
                    team.removeEntry(player.getName());

                    Util.getOnlineMembers(team.getEntries(), null).forEach(p -> {
                        final String msg = (String) plugin.getMessages().get(MessageInfo.PLAYER_LEFT.get());
                        p.sendMessage(ChatColor.RED + msg.formatted(player.getName()));
                    });

                    player.sendMessage((String) plugin.getMessages().get(MessageInfo.PARTY_LEFT.get()));
                })
                .register();
    }
}
