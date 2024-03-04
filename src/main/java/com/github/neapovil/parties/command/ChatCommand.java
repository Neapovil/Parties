package com.github.neapovil.parties.command;

import java.util.Set;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ChatCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.chat")
                .withArguments(new LiteralArgument("chat").withRequirement(sender -> {
                    return Util.getParty((Player) sender).isPresent();
                }))
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    final String message = (String) args.get("message");

                    final String rankname = Util.getRank(player).title();

                    Util.getOnlineMembers(player).forEach(i -> {
                        if (Set.of(PartyRank.LEADER, PartyRank.MOD).contains(Util.getRank(player)))
                        {
                            i.sendRichMessage("<green>PARTY <gray>[%s :: %s] >> <dark_gray>%s".formatted(rankname, player.getName(), message));
                        }
                        else
                        {
                            i.sendRichMessage("<green>PARTY <gray>[%s] >> <dark_gray>%s".formatted(player.getName(), message));
                        }
                    });
                })
                .register();
    }
}
