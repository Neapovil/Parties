package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class LeaveCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.leave")
                .withArguments(new LiteralArgument("leave").withRequirement(sender -> {
                    return Util.getParty((Player) sender).isPresent() && !Util.getRank((Player) sender).equals(PartyRank.LEADER);
                }))
                .executesPlayer((player, args) -> {
                    final Team team = Util.getParty(player).get();

                    Util.getOnlineMembers(player).forEach(i -> {
                        if (!i.getName().equals(player.getName()))
                        {
                            i.sendRichMessage("<red>%s left the party".formatted(player.getName()));
                        }
                    });

                    player.getPersistentDataContainer().remove(plugin.partyIdKey);
                    team.removeEntry(player.getName());
                    team.removeEntry("mod-" + player.getName());

                    player.sendRichMessage("<red>You left the party");

                    CommandAPI.updateRequirements(player);
                })
                .register();
    }
}
