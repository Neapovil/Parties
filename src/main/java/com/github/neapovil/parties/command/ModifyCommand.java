package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.github.neapovil.parties.resource.PartiesResource;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ModifyCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.modify.friendlyfire")
                .withArguments(new LiteralArgument("modify").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party.Member> optionalmember = plugin.findMember(player);
                    return optionalmember.isPresent() && optionalmember.get().role.isLeader();
                }))
                .withArguments(new LiteralArgument("friendlyFire"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args.get("value");

                    plugin.findParty(player).ifPresent(party -> {
                        party.team().setAllowFriendlyFire(value);

                        party.onlineMembers().forEach(i -> i.sendMessage("Party friendly fire status changed to: " + value));
                    });
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.modify.collision")
                .withArguments(new LiteralArgument("modify").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party> optionalparty = plugin.findParty(player);
                    return optionalparty.isPresent() && optionalparty.get().findMember(player).get().role.isLeader();
                }))
                .withArguments(new LiteralArgument("collision"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args.get("value");

                    plugin.findParty(player).ifPresent(party -> {
                        if (value)
                        {
                            party.team().setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
                        }
                        else
                        {
                            party.team().setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
                        }

                        party.onlineMembers().forEach(i -> i.sendMessage("Party collisions status changed to: " + value));
                    });
                })
                .register();
    }
}
