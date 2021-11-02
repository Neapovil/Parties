package com.github.nearata.parties.command;

import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class ModifyCommand
{
    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.modify.friendlyfire")
                .withArguments(new LiteralArgument("modify"))
                .withArguments(new LiteralArgument("friendlyFire"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        Messages.SENDER_NO_PERMISSIONS_ONLY_LEADER.fail();
                    }

                    Util.getParty(player).get().setAllowFriendlyFire(value);

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (value)
                        {
                            Messages.PARTY_FRIENDLY_FIRE_ENABLED.send(p);
                        }
                        else
                        {
                            Messages.PARTY_FRIENDLY_FIRE_DISABLED.send(p);
                        }
                    });
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.modify.collision")
                .withArguments(new LiteralArgument("modify"))
                .withArguments(new LiteralArgument("collision"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        Messages.SENDER_NO_PERMISSIONS_ONLY_LEADER.fail();
                    }

                    if (value)
                    {
                        Util.getParty(player).get().setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
                    }
                    else
                    {
                        Util.getParty(player).get().setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
                    }

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (value)
                        {
                            Messages.PARTY_COLLISION_ENABLED.send(p);
                        }
                        else
                        {
                            Messages.PARTY_COLLISION_DISABLED.send(p);
                        }
                    });
                })
                .register();
    }
}
