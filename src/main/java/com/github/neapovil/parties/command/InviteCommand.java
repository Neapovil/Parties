package com.github.neapovil.parties.command;

import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.object.PartyInvite;
import com.github.neapovil.parties.resource.PartiesResource;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;

public final class InviteCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.invite")
                .withArguments(new LiteralArgument("invite").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party.Member> optionalmember = plugin.findMember(player);
                    return optionalmember.isPresent() && optionalmember.get().role.hasPermission("invite");
                }))
                .withArguments(new PlayerArgument("player").replaceSafeSuggestions(SafeSuggestions.suggest(info -> {
                    final Player player = (Player) info.sender();
                    return Stream.of(plugin.getServer().getOnlinePlayers().toArray(Player[]::new))
                            .filter(i -> !i.getUniqueId().equals(player.getUniqueId()))
                            .filter(i -> plugin.findParty(i).isEmpty())
                            .filter(i -> !plugin.invites.containsKey(i.getUniqueId()))
                            .toArray(Player[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final Player player1 = (Player) args.get("player");

                    if (player.getUniqueId().equals(player1.getUniqueId()))
                    {
                        return;
                    }

                    if (plugin.findParty(player1).isPresent())
                    {
                        return;
                    }

                    if (plugin.invites.containsKey(player1.getUniqueId()))
                    {
                        return;
                    }

                    plugin.findParty(player).ifPresent(party -> {
                        plugin.invites.put(player1.getUniqueId(), new PartyInvite(party, player.getName()));
                        player.sendMessage("You invited %s to join your party".formatted(player1.getName()));
                        player1.sendMessage("%s invited you to join their party".formatted(player.getName()));
                    });
                })
                .register();
    }
}
