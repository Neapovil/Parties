package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ModifyCommand implements ICommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.modify.friendlyfire")
                .withArguments(new LiteralArgument("modify").withRequirement(sender -> {
                    return Util.getRank((Player) sender).equals(PartyRank.LEADER);
                }))
                .withArguments(new LiteralArgument("friendlyFire"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args.get("value");

                    Util.getParty(player).get().setAllowFriendlyFire(value);

                    Util.getOnlineMembers(player).forEach(i -> {
                        i.sendMessage("Party friendly fire status changed to: " + value);
                    });
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.modify.collision")
                .withArguments(new LiteralArgument("modify").withRequirement(sender -> {
                    return Util.getRank((Player) sender).equals(PartyRank.LEADER);
                }))
                .withArguments(new LiteralArgument("collision"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args.get("value");

                    if (value)
                    {
                        Util.getParty(player).get().setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
                    }
                    else
                    {
                        Util.getParty(player).get().setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
                    }

                    Util.getOnlineMembers(player).forEach(i -> {
                        i.sendMessage("Party collisions status changed to: " + value);
                    });
                })
                .register();
    }
}
