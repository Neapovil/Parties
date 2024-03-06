package com.github.neapovil.parties.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.resource.PartiesResource;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ListCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.list")
                .withArguments(new LiteralArgument("list").withRequirement(sender -> {
                    return plugin.findParty((Player) sender).isPresent();
                }))
                .executesPlayer((player, args) -> {
                    plugin.findParty(player).ifPresent(party -> {
                        String leader = "";
                        final List<String> moderators = new ArrayList<>();
                        final List<String> members = new ArrayList<>();

                        for (PartiesResource.Party.Member i : party.members)
                        {
                            if (i.role.isLeader())
                            {
                                leader = i.username;
                                continue;
                            }

                            if (i.role.isMod())
                            {
                                moderators.add(i.username);
                                continue;
                            }

                            members.add(i.username);
                        }

                        player.sendMessage("Party Leader: %s".formatted(leader));
                        player.sendMessage("Party Moderators: %s".formatted(String.join(", ", moderators)));
                        player.sendMessage("Party Members: %s".formatted(String.join(", ", members)));
                    });
                })
                .register();
    }
}
