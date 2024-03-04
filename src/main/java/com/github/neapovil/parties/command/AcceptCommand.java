package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class AcceptCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.accept")
                .withArguments(new LiteralArgument("accept").withRequirement(sender -> {
                    return Util.getParty((Player) sender).isEmpty();
                }))
                .withArguments(new StringArgument("playerName").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    return plugin.getManager()
                            .getInvites()
                            .values()
                            .stream()
                            .filter(i -> i.getUUID().equals(((Player) info.sender()).getUniqueId()))
                            .map(i -> i.getIssuer())
                            .toArray(String[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final String issuer = (String) args.get("playerName");

                    if (Util.getParty(player).isPresent())
                    {
                        return;
                    }

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
                        throw CommandAPI.failWithString("The invite has expired");
                    }

                    plugin.getManager()
                            .getInvites()
                            .values()
                            .removeIf(i -> {
                                return i.getIssuer().equals(issuer)
                                        && i.getUUID().equals(player.getUniqueId());
                            });

                    plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid.get()).addEntry(player.getName());
                    player.getPersistentDataContainer().set(plugin.partyIdKey, PersistentDataType.STRING, partyid.get());

                    Util.getOnlineMembers(player).forEach(i -> {
                        if (!i.getName().equals(player.getName()))
                        {
                            i.sendMessage("%s joined the party".formatted(player.getName()));
                        }
                    });

                    player.sendMessage("You joined the party");

                    CommandAPI.updateRequirements(player);
                })
                .register();
    }
}
