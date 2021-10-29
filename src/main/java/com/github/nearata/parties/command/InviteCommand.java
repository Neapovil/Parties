package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.object.PartyInvite;

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

                    if (!player.getPersistentDataContainer().has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessages().get(MessageError.NO_PARTY.get()));
                    }

                    final String partyid = player.getPersistentDataContainer().get(plugin.getKey(), plugin.getKeyType());
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    if (!team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail((String) plugin.getMessages().get(MessageError.CANNOT_INVITE_NO_PERMISSIONS.get()));
                    }

                    final Player player1 = (Player) args[0];
                    final UUID uuid1 = player1.getUniqueId();

                    if (uuid.equals(uuid1))
                    {
                        CommandAPI.fail(plugin.getMessages().get(MessageError.CANNOT_SELF_INVITE.get()));
                    }

                    if (player1.getPersistentDataContainer().has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessages().get(MessageError.INVITED_PLAYER_HAS_PARTY.get()));
                    }

                    if (plugin.getManager().getInvites().get(partyid).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        CommandAPI.fail(plugin.getMessages().get(MessageError.ALREADY_INVITED.get()));
                    }

                    plugin.getManager().getInvites().put(partyid, new PartyInvite(player.getName(), uuid1));

                    player.sendMessage(((String) plugin.getMessages().get(MessageInfo.INVITED.get())).formatted(player1.getName()));
                    player1.sendMessage(((String) plugin.getMessages().get(MessageInfo.INVITED_BY.get())).formatted(player.getName()));
                })
                .register();
    }
}
