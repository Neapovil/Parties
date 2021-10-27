package com.github.nearata.parties.command;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.nearata.parties.Parties;
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
                        CommandAPI.fail(plugin.getMessages().get("errors.no_party"));
                    }

                    final Player player1 = (Player) args[0];

                    if (player1.getPersistentDataContainer().has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessages().get("errors.invited_player_has_party"));
                    }

                    final UUID uuid1 = player1.getUniqueId();

                    if (uuid.equals(uuid1))
                    {
                        CommandAPI.fail(plugin.getMessages().get("errors.cannot_self_invite"));
                    }

                    final String partyid = player.getPersistentDataContainer().get(plugin.getKey(), plugin.getKeyType());

                    if (plugin.getManager().getInvites().get(partyid).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        CommandAPI.fail(plugin.getMessages().get("errors.already_invited"));
                    }

                    plugin.getManager().getInvites().put(partyid, new PartyInvite(player.getName(), uuid1));

                    player.sendMessage(((String) plugin.getMessages().get("info.invited")).formatted(player1.getName()));
                    player1.sendMessage(((String) plugin.getMessages().get("info.invited_by")).formatted(player.getName()));
                })
                .register();
    }
}
