package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.runnable.SaveRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class AcceptCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.accept")
                .withArguments(new LiteralArgument("accept").withRequirement(sender -> {
                    return plugin.findParty((Player) sender).isEmpty();
                }))
                .withArguments(new StringArgument("leaderName").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    final Player player = (Player) info.sender();
                    return plugin.invites.get(player.getUniqueId())
                            .stream()
                            .map(i -> i.leaderName)
                            .toArray(String[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final String leadername = (String) args.get("leaderName");

                    if (plugin.findParty(player).isPresent())
                    {
                        return;
                    }

                    final Optional<PartiesResource.Party> optionalparty = plugin.invites.get(player.getUniqueId())
                            .stream()
                            .filter(i -> i.leaderName.equalsIgnoreCase(leadername))
                            .map(i -> i.party)
                            .findFirst();

                    optionalparty.ifPresentOrElse((party) -> {
                        plugin.invites.get(player.getUniqueId()).removeIf(i -> i.leaderName.equalsIgnoreCase(leadername));

                        party.add(player);
                        player.getPersistentDataContainer().set(plugin.partyIdKey, Parties.UUID_DATA_TYPE, party.uuid);

                        party.onlineMembers().forEach(i -> {
                            if (!i.getName().equals(player.getName()))
                            {
                                i.sendMessage("%s joined the party".formatted(player.getName()));
                            }
                        });

                        player.sendMessage("You joined the party");

                        CommandAPI.updateRequirements(player);

                        new SaveRunnable().runTaskAsynchronously(plugin);
                    }, () -> player.sendRichMessage("<red>The invite has expired"));
                })
                .register();
    }
}
