package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.runnable.SaveRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class LeaveCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.leave")
                .withArguments(new LiteralArgument("leave").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party.Member> optionalmember = plugin.findMember(player);
                    return optionalmember.isPresent() && !optionalmember.get().role.isLeader();
                }))
                .executesPlayer((player, args) -> {
                    final Optional<PartiesResource.Party> optionalparty = plugin.findParty(player);

                    optionalparty.ifPresent(party -> {
                        party.onlineMembers().forEach(i -> {
                            if (!i.getUniqueId().equals(player.getUniqueId()))
                            {
                                i.sendRichMessage("<red>%s left the party".formatted(player.getName()));
                            }
                        });

                        party.findMember(player).ifPresent(member -> {
                            player.getPersistentDataContainer().remove(plugin.partyIdKey);
                            party.team().removeEntry(player.getName());
                            party.members.removeIf(i -> i.uuid.equals(player.getUniqueId()));

                            player.sendRichMessage("<red>You left the party");

                            CommandAPI.updateRequirements(player);

                            new SaveRunnable().runTaskAsynchronously(plugin);
                        });
                    });
                })
                .register();
    }
}
