package com.github.nearata.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

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
                    return plugin.getPartiesManager()
                            .getInvites()
                            .values()
                            .stream()
                            .filter(i -> i.getUUID().equals(((Player) s.sender()).getUniqueId()))
                            .map(i -> i.getIssuer())
                            .toArray(String[]::new);
                }))
                .executesPlayer((player, args) -> {
                    if (player.getPersistentDataContainer().has(plugin.getKey(), plugin.getKeyType()))
                    {
                        CommandAPI.fail(plugin.getMessagesConfig().get("errors.has_party"));
                    }

                    final String issuer = (String) args[0];

                    final Optional<String> partyid = plugin.getPartiesManager()
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
                        CommandAPI.fail(plugin.getMessagesConfig().get("errors.expired_invite"));
                    }

                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid.get());

                    for (String username : team.getEntries())
                    {
                        final Player partyplayer = plugin.getServer().getPlayer(username);

                        if (partyplayer == null)
                        {
                            continue;
                        }

                        partyplayer.sendMessage(ChatColor.GREEN + ((String) plugin.getMessagesConfig().get("info.player_joined")).formatted(player.getName()));
                    }

                    team.addEntry(player.getName());
                    player.getPersistentDataContainer().set(plugin.getKey(), plugin.getKeyType(), partyid.get());
                    plugin.getPartiesManager()
                            .getInvites()
                            .values()
                            .removeIf(i -> {
                                return i.getIssuer().equals(issuer)
                                        && i.getUUID().equals(player.getUniqueId());
                            });

                    player.sendMessage(ChatColor.GREEN + (String) plugin.getMessagesConfig().get("info.party_joined"));
                })
                .register();
    }
}
