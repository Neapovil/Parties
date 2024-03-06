package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.runnable.SaveRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class DisbandCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party.Member> optionalmember = plugin.findMember(player);
                    return optionalmember.isPresent() && optionalmember.get().role.isLeader();
                }))
                .executesPlayer((player, args) -> {
                    plugin.findParty(player).ifPresent(party -> {
                        party.onlineMembers().forEach(i -> {
                            if (!i.getUniqueId().equals(player.getUniqueId()))
                            {
                                i.getPersistentDataContainer().remove(plugin.partyIdKey);
                                i.sendRichMessage("<red>The party has been disbanded");
                                CommandAPI.updateRequirements(i);
                            }
                        });

                        party.team().unregister();
                        plugin.partiesResource.parties.removeIf(i -> i.uuid.equals(party.uuid));
                        player.getPersistentDataContainer().remove(plugin.partyIdKey);

                        player.sendRichMessage("<red>You disbanded the party");

                        CommandAPI.updateRequirements(player);

                        new SaveRunnable().runTaskAsynchronously(plugin);
                    });
                })
                .register();
    }
}
