package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;

public final class GotoCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.goto")
                .withArguments(new LiteralArgument("goto").withRequirement(sender -> {
                    return plugin.findParty((Player) sender).isPresent();
                }))
                .withArguments(new PlayerArgument("player").replaceSafeSuggestions(SafeSuggestions.suggest(info -> {
                    final Player player = (Player) info.sender();
                    return plugin.findParty(player).get().onlineMembers()
                            .stream()
                            .filter(i -> !i.getUniqueId().equals(player.getUniqueId()))
                            .toArray(Player[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final Player player1 = (Player) args.get("player");

                    if (player.getUniqueId().equals(player1.getUniqueId()))
                    {
                        return;
                    }

                    plugin.findParty(player).ifPresent(party -> {
                        if (party.onlineMembers().stream().anyMatch(i -> i.getUniqueId().equals(player1.getUniqueId())))
                        {
                            plugin.partyGoto.put(player.getUniqueId(), player1.getUniqueId());
                        }
                    });
                })
                .register();
    }
}
