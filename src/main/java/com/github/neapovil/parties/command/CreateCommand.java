package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class CreateCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.create")
                .withArguments(new LiteralArgument("create").withRequirement(sender -> {
                    return Util.getParty((Player) sender).isEmpty();
                }))
                .executesPlayer((player, args) -> {
                    final String partyid = player.getUniqueId().toString();

                    player.getPersistentDataContainer().set(plugin.partyIdKey, PersistentDataType.STRING, partyid);

                    final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(partyid);
                    team.addEntry(player.getName());
                    team.addEntry("leader-" + player.getName());
                    team.setAllowFriendlyFire(false);

                    player.sendMessage("Party created");

                    CommandAPI.updateRequirements(player);
                })
                .register();
    }
}
