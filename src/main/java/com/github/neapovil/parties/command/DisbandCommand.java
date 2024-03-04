package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class DisbandCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband").withRequirement(sender -> {
                    return Util.getRank((Player) sender).equals(PartyRank.LEADER);
                }))
                .executesPlayer((player, args) -> {
                    final PersistentDataContainer data = player.getPersistentDataContainer();
                    final String partyid = data.get(plugin.partyIdKey, PersistentDataType.STRING);
                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    Util.getOnlineMembers(player).forEach(i -> {
                        if (!i.getName().equals(player.getName()))
                        {
                            i.getPersistentDataContainer().remove(plugin.partyIdKey);
                            i.sendMessage("The party has been disbanded");
                            CommandAPI.updateRequirements(i);
                        }
                    });

                    data.remove(plugin.partyIdKey);
                    team.unregister();

                    player.sendMessage("You disbanded the party");

                    CommandAPI.updateRequirements(player);
                })
                .register();
    }
}
