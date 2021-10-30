package com.github.nearata.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class AcceptCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.accept")
                .withArguments(new LiteralArgument("accept"))
                .withArguments(new StringArgument("player").replaceSuggestions(s -> {
                    return plugin.getManager()
                            .getInvites()
                            .values()
                            .stream()
                            .filter(i -> i.getUUID().equals(((Player) s.sender()).getUniqueId()))
                            .map(i -> i.getIssuer())
                            .toArray(String[]::new);
                }))
                .executesPlayer((player, args) -> {
                    if (Util.getParty(player).isPresent())
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.HAS_PARTY.get()));
                    }

                    final String issuer = (String) args[0];

                    final Optional<String> partyid = plugin.getManager()
                            .getInvites()
                            .entries()
                            .stream()
                            .filter(e -> {
                                return e.getValue().getIssuer().equals(issuer)
                                        && e.getValue().getUUID().equals(player.getUniqueId());
                            })
                            .map(e -> e.getKey())
                            .findAny();

                    if (partyid.isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_INVITED_EXPIRED.get()));
                    }

                    plugin.getManager()
                            .getInvites()
                            .values()
                            .removeIf(i -> {
                                return i.getIssuer().equals(issuer)
                                        && i.getUUID().equals(player.getUniqueId());
                            });

                    plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid.get()).addEntry(player.getName());
                    player.getPersistentDataContainer().set(plugin.getKey(), plugin.getKeyType(), partyid.get());

                    final String msg = plugin.getMessage(Messages.PARTY_INVITED_JOINED.get()).formatted(player.getName());
                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        p.sendMessage(msg);
                    });

                    player.sendMessage(plugin.getMessage(Messages.SENDER_INVITED_JOINED.get()));
                })
                .register();
    }
}
