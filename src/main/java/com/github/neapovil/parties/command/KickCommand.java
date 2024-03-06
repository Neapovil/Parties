package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.runnable.SaveRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class KickCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.kick")
                .withArguments(new LiteralArgument("kick").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party.Member> optionalmember = plugin.findMember(player);
                    return optionalmember.isPresent() && optionalmember.get().role.hasPermission("kick");
                }))
                .withArguments(new StringArgument("playerName").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    final Player player = (Player) info.sender();
                    return plugin.findParty(player).get().members
                            .stream()
                            .filter(i -> !i.role.hasPermission("kick"))
                            .map(i -> i.username)
                            .filter(i -> !i.equalsIgnoreCase(player.getName()))
                            .toArray(String[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final String playername = (String) args.get("playerName");

                    if (playername.equalsIgnoreCase(player.getName()))
                    {
                        return;
                    }

                    plugin.findParty(player).ifPresent(party -> {
                        party.findMember(playername).ifPresent(member -> {
                            if (member.role.hasPermission("kick"))
                            {
                                player.sendRichMessage("<red>You can't kick this player");
                                return;
                            }

                            party.members.removeIf(i -> i.uuid.equals(member.uuid));
                            party.team().removeEntry(playername);

                            final Player player1 = plugin.getServer().getPlayer(playername);

                            if (player1 != null)
                            {
                                player1.getPersistentDataContainer().remove(plugin.partyIdKey);
                                player1.sendRichMessage("<red>You have been kicked from the party");
                                CommandAPI.updateRequirements(player1);
                            }

                            party.onlineMembers().forEach(i -> {
                                if (!i.getName().equals(player.getName()))
                                {
                                    i.sendRichMessage("<red>%s has been kicked from the party".formatted(playername));
                                }
                            });

                            player.sendRichMessage("<red>You kicked %s from the party".formatted(playername));

                            new SaveRunnable().runTaskAsynchronously(plugin);
                        });
                    });
                })
                .register();
    }
}
