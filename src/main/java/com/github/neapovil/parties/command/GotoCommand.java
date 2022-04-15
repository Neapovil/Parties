package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.messages.Messages;
import com.github.neapovil.parties.util.Util;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class GotoCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.goto")
                .withArguments(new LiteralArgument("goto"))
                .withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getOnlineMembers(player)
                            .stream()
                            .filter(p -> !p.getName().equals(player.getName()))
                            .map(p -> p.getName())
                            .toArray(String[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final String playername = (String) args[0];
                    final Player player1 = plugin.getServer().getPlayer(playername);

                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    if (player1 == null)
                    {
                        Messages.SENDER_PLAYER_NOT_FOUND.fail();
                    }

                    if (player.getName().equals(playername))
                    {
                        Messages.SENDER_CANNOT_GOT_SELF.fail();
                    }

                    if (!Util.getMembers(player).contains(playername))
                    {
                        Messages.SENDER_PLAYER_NOT_IN_PARTY.fail();
                    }

                    plugin.getManager().getGoto().put(player.getUniqueId(), player1.getUniqueId());
                })
                .register();
    }
}
