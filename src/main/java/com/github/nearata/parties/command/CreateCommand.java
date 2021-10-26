package com.github.nearata.parties.command;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.md_5.bungee.api.ChatColor;

public final class CreateCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.create")
                .withArguments(new LiteralArgument("create"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    if (player.getPersistentDataContainer().has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessagesConfig().get("errors.has_party"));
                    }

                    final String partyid = StringUtils.left(uuid.toString().replace("-", ""), 16);
                    player.getPersistentDataContainer().set(plugin.getKey(), plugin.getKeyType(), partyid);

                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(partyid);
                    team.addEntry(player.getName());
                    team.setAllowFriendlyFire(false);

                    player.sendMessage(ChatColor.GREEN + (String) plugin.getMessagesConfig().get("info.party_created"));
                })
                .register();
    }
}
