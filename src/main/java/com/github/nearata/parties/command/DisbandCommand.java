package com.github.nearata.parties.command;

import java.util.UUID;

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

public final class DisbandCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    final PersistentDataContainer data = player.getPersistentDataContainer();

                    if (!data.has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessages().get(MessageError.NO_PARTY.get()));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    if (!team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail((String) plugin.getMessages().get(MessageError.CANNOT_DISBAND_NOT_LEADER.get()));
                    }

                    data.remove(plugin.getKey());

                    Util.getOnlineMembers(team.getEntries(), uuid).forEach(p -> {
                        p.getPersistentDataContainer().remove(plugin.getKey());
                        p.sendMessage(ChatColor.RED + (String) plugin.getMessages().get(MessageInfo.PARTY_DISBANDED_BY.get()));
                    });

                    team.unregister();
                    player.sendMessage(ChatColor.RED + (String) plugin.getMessages().get(MessageInfo.PARTY_DISBANDED.get()));
                })
                .register();
    }
}
