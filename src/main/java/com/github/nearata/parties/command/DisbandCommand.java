package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
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
                        CommandAPI.fail(plugin.getMessages().get("errors.no_party"));
                    }

                    final String partyid = data.get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    if (!team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail((String) plugin.getMessages().get("errors.cannot_disband_not_leader"));
                    }

                    data.remove(plugin.getKey());

                    Util.getOnlineMembers(team.getEntries(), uuid).forEach(p -> {
                        p.getPersistentDataContainer().remove(plugin.getKey());
                        p.sendMessage(ChatColor.RED + (String) plugin.getMessages().get("info.party_disbanded_by"));
                    });

                    team.unregister();
                    player.sendMessage(ChatColor.RED + (String) plugin.getMessages().get("info.party_disbanded"));
                })
                .register();
    }
}
