package com.github.neapovil.parties.command;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.messages.Messages;
import com.github.neapovil.parties.util.Util;

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
                        Messages.HAS_PARTY.fail();
                    }

                    final String partyid = StringUtils.left(uuid.toString().replace("-", ""), 16);
                    player.getPersistentDataContainer().set(plugin.getKey(), plugin.getKeyType(), partyid);

                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(partyid);
                    team.addEntry(player.getName());
                    team.addEntry("leader-" + player.getName());
                    team.setAllowFriendlyFire(false);

                    Messages.SENDER_PARTY_CREATED.send(player);
                })
                .register();
    }
}
