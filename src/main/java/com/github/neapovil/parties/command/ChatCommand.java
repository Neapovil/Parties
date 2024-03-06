package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ChatCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.chat")
                .withArguments(new LiteralArgument("chat").withRequirement(sender -> {
                    return plugin.findParty((Player) sender).isPresent();
                }))
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    final String message = (String) args.get("message");

                    plugin.findParty(player).ifPresent(party -> {
                        party.findMember(player).ifPresent(member -> {
                            party.onlineMembers().forEach(i -> {
                                if (member.role.prefix() == null)
                                {
                                    i.sendRichMessage("<green>PARTY <gray>[%s] >> <white>%s".formatted(player.getName(), message));
                                }
                                else
                                {
                                    i.sendRichMessage(
                                            "<green>PARTY <gray>[%s :: %s] >> <white>%s".formatted(member.role.prefix(), player.getName(), message));
                                }
                            });
                        });
                    });
                })
                .register();
    }
}
