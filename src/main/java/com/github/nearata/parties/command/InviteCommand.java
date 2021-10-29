package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.object.PartyInvite;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;

public final class InviteCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.invite")
                .withArguments(new LiteralArgument("invite"))
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();

                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();

                    if (!team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PERMISSIONS.get()));
                    }

                    final Player player1 = (Player) args[0];
                    final UUID uuid1 = player1.getUniqueId();

                    if (uuid.equals(uuid1))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.CANNOT_SELF_INVITE.get()));
                    }

                    if (Util.getParty(player1).isPresent())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.INVITED_PLAYER_HAS_PARTY.get()));
                    }

                    if (plugin.getManager().getInvites().get(team.getName()).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.ALREADY_INVITED.get()));
                    }

                    plugin.getManager().getInvites().put(team.getName(), new PartyInvite(player.getName(), uuid1));

                    player.sendMessage(plugin.getMessage(MessageInfo.INVITED.get()).formatted(player1.getName()));
                    player1.sendMessage(plugin.getMessage(MessageInfo.INVITED_BY.get()).formatted(player.getName()));
                })
                .register();
    }
}
