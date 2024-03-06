package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.runnable.SaveRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class CreateCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.create")
                .withArguments(new LiteralArgument("create").withRequirement(sender -> {
                    return plugin.findParty((Player) sender).isEmpty();
                }))
                .executesPlayer((player, args) -> {
                    plugin.partiesResource.parties.add(PartiesResource.create(player));

                    player.sendMessage("Party created");

                    CommandAPI.updateRequirements(player);

                    new SaveRunnable().runTaskAsynchronously(plugin);
                })
                .register();
    }
}
