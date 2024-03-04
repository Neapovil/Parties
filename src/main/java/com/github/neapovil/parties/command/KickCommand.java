package com.github.neapovil.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class KickCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.kick")
                .withArguments(new LiteralArgument("kick").withRequirement(sender -> {
                    final PartyRank partyrank = Util.getRank((Player) sender);
                    return partyrank.equals(PartyRank.LEADER) || partyrank.equals(PartyRank.MOD);
                }))
                .withArguments(new StringArgument("playerName").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getMembers(player)
                            .stream()
                            .filter(i -> !i.equalsIgnoreCase(player.getName()))
                            .toArray(String[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final String playername = (String) args.get("playerName");

                    if (playername.equals(player.getName()))
                    {
                        throw CommandAPI.failWithString("You can't kick yourself");
                    }

                    final Team team = Util.getParty(player).get();

                    if (!Util.getRank(player).equals(PartyRank.LEADER) && team.getEntries().contains("mod-" + playername))
                    {
                        throw CommandAPI.failWithString("Only the leader can kick a mod");
                    }

                    if (team.getEntries().contains("leader-" + playername))
                    {
                        throw CommandAPI.failWithString("You can't kick the leader");
                    }

                    if (!team.getEntries().contains(playername))
                    {
                        throw CommandAPI.failWithString("Player not found");
                    }

                    team.removeEntry(playername);
                    team.removeEntry("mod" + playername);

                    final Player player1 = plugin.getServer().getPlayer(playername);

                    if (player1 != null)
                    {
                        player1.getPersistentDataContainer().remove(plugin.partyIdKey);
                        player1.sendRichMessage("<red>You have been kicked from the party");
                        CommandAPI.updateRequirements(player1);
                    }

                    Util.getOnlineMembers(player).forEach(i -> {
                        if (!i.getName().equals(player.getName()))
                        {
                            i.sendMessage("<red>%s has been kicked from the party".formatted(playername));
                        }
                    });

                    player.sendMessage("<red>You kicked %s from the party".formatted(playername));
                })
                .register();
    }
}
