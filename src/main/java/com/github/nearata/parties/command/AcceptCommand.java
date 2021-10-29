package com.github.nearata.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.md_5.bungee.api.ChatColor;

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
                        CommandAPI.fail(plugin.getMessage(MessageError.HAS_PARTY.get()));
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
                        CommandAPI.fail(plugin.getMessage(MessageError.EXPIRED_INVITE.get()));
                    }

                    plugin.getManager()
                            .getInvites()
                            .values()
                            .removeIf(i -> {
                                return i.getIssuer().equals(issuer)
                                        && i.getUUID().equals(player.getUniqueId());
                            });

                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid.get());

                    final String msg = plugin.getMessage(MessageInfo.PLAYER_JOINED.get()).formatted(player.getName());
                    Util.getOnlineMembers(team.getEntries(), null).forEach(p -> {
                        p.sendMessage(ChatColor.GREEN + msg);
                    });

                    team.addEntry(player.getName());
                    player.getPersistentDataContainer().set(plugin.getKey(), plugin.getKeyType(), partyid.get());

                    player.sendMessage(ChatColor.GREEN + plugin.getMessage(MessageInfo.PARTY_JOINED.get()));
                })
                .register();
    }
}
