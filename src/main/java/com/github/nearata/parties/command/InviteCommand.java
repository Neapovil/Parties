package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.object.PartyInvite;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

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
                        Messages.NO_PARTY.fail();
                    }

                    final Team team = Util.getParty(player).get();

                    if (!(Util.getRank(player).equals(PartyRank.MOD) || Util.getRank(player).equals(PartyRank.LEADER)))
                    {
                        Messages.SENDER_NO_PERMISSIONS.fail();
                    }

                    final Player player1 = (Player) args[0];
                    final UUID uuid1 = player1.getUniqueId();

                    if (uuid.equals(uuid1))
                    {
                        Messages.SENDER_CANNOT_SELF_INVITED.fail();
                    }

                    if (Util.getParty(player1).isPresent())
                    {
                        Messages.SENDER_INVITED_PLAYER_HAS_PARTY.fail();
                    }

                    if (plugin.getManager().getInvites().get(team.getName()).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        Messages.SENDER_PLAYER_ALREADY_INVITED.fail();
                    }

                    plugin.getManager().getInvites().put(team.getName(), new PartyInvite(player.getName(), uuid1));

                    Messages.SENDER_PLAYER_INVITED.send(player, player1.getName());
                    Messages.PLAYER_INVITED.send(player1, player.getName());
                })
                .register();
    }
}
