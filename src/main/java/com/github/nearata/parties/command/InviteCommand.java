package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.object.PartyInvite;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

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
                        CommandAPI.fail(plugin.getMessage(Messages.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();

                    if (!(Util.getRank(player).equals(PartyRank.MOD) || Util.getRank(player).equals(PartyRank.LEADER)))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_NO_PERMISSIONS.get()));
                    }

                    final Player player1 = (Player) args[0];
                    final UUID uuid1 = player1.getUniqueId();

                    if (uuid.equals(uuid1))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_SELF_INVITED.get()));
                    }

                    if (Util.getParty(player1).isPresent())
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_INVITED_PLAYER_HAS_PARTY.get()));
                    }

                    if (plugin.getManager().getInvites().get(team.getName()).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_PLAYER_ALREADY_INVITED.get()));
                    }

                    plugin.getManager().getInvites().put(team.getName(), new PartyInvite(player.getName(), uuid1));

                    player.sendMessage(plugin.getMessage(Messages.SENDER_PLAYER_INVITED.get()).formatted(player1.getName()));
                    player1.sendMessage(plugin.getMessage(Messages.PLAYER_INVITED.get()).formatted(player.getName()));
                })
                .register();
    }
}
