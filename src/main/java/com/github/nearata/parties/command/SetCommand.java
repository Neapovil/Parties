package com.github.nearata.parties.command;

import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class SetCommand
{
    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.set")
                .withArguments(new LiteralArgument("set"))
                .withArguments(new LiteralArgument("allowFriendlyFire"))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer((player, args) -> {
                    final boolean value = (boolean) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        Messages.SENDER_NO_PERMISSIONS.fail();
                    }

                    Util.getParty(player).get().setAllowFriendlyFire(value);

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (value)
                        {
                            Messages.PARTY_ALLOW_FRIENDLY_FIRE_ENABLED.send(p);
                        }
                        else
                        {
                            Messages.PARTY_ALLOW_FRIENDLY_FIRE_DISABLED.send(p);
                        }
                    });
                })
                .register();
    }
}
