package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;

public final class GotoCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.goto")
                .withArguments(new LiteralArgument("goto").withRequirement(sender -> {
                    return Util.getParty((Player) sender).isPresent();
                }))
                .withArguments(new PlayerArgument("player").replaceSafeSuggestions(SafeSuggestions.suggest(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getOnlineMembers(player)
                            .stream()
                            .filter(i -> !i.getName().equals(player.getName()))
                            .toArray(Player[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final Player player1 = (Player) args.get("player");

                    if (player.getUniqueId().equals(player1.getUniqueId()))
                    {
                        return;
                    }

                    if (!Util.getMembers(player).contains(player1.getName()))
                    {
                        return;
                    }

                    plugin.getManager().getGoto().put(player.getUniqueId(), player1.getUniqueId());
                })
                .register();
    }
}
