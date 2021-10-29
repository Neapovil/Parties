package com.github.nearata.parties.command;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

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

                    if (Util.getParty(player).isPresent())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.HAS_PARTY.get()));
                    }

                    final String partyid = StringUtils.left(uuid.toString().replace("-", ""), 16);
                    player.getPersistentDataContainer().set(plugin.getKey(), plugin.getKeyType(), partyid);

                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(partyid);
                    team.addEntry(player.getName());
                    team.addEntry("leader-" + player.getName());
                    team.setAllowFriendlyFire(false);

                    player.sendMessage(plugin.getMessage(MessageInfo.PARTY_CREATED.get()));
                })
                .register();
    }
}
