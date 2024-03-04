package com.github.neapovil.parties.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class PromoteCommand implements ICommand
{
    private final Parties plugin = Parties.instance();
    private final List<String> ranks = new ArrayList<>(List.of("leader", "mod"));

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.promote")
                .withArguments(new MultiLiteralArgument("command", "promote", "demote").withRequirement(sender -> {
                    return Util.getRank((Player) sender).equals(PartyRank.LEADER);
                }))
                .withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getMembers(player)
                            .stream()
                            .filter(i -> !i.startsWith(player.getName()))
                            .filter(i -> !i.startsWith("mod-") || !i.startsWith("leader-"))
                            .toArray(String[]::new);
                })))
                .withArguments(new StringArgument("rank").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    if (info.previousArgs().get("command").equals("promote"))
                    {
                        return ranks.toArray(String[]::new);
                    }

                    return new String[] { "member" };
                })))
                .executesPlayer((player, args) -> {
                    final String command = (String) args.get("command");
                    final String player1name = (String) args.get("player");
                    final String rank = (String) args.get("rank");

                    if (player.getName().equals(player1name))
                    {
                        return;
                    }

                    final Team team = Util.getParty(player).get();

                    if (!team.getEntries().contains(player1name))
                    {
                        return;
                    }

                    String player1message = "";

                    if (command.equals("promote"))
                    {
                        if (rank.equals("leader"))
                        {
                            team.removeEntry("leader-" + player.getName());
                            team.removeEntry("mod-" + player1name);
                            team.addEntry(rank + "-" + player1name);
                            player1message = "You have been promoted party leader";
                        }

                        if (rank.equals("mod"))
                        {
                            team.addEntry(rank + "-" + player1name);
                            player1message = "You have been promoted party moderator";
                        }
                    }

                    if (command.equals("demote"))
                    {
                        team.removeEntry("mod-" + player1name);
                        player1message = "You have been demoted to party member";
                    }

                    Util.getOnlineMembers(player).forEach(i -> {
                        if (i.getName().equals(player.getName()))
                        {
                            return;
                        }

                        if (i.getName().equals(player1name))
                        {
                            return;
                        }

                        i.sendMessage("%s's party rank changed to: %s".formatted(player1name, rank));
                    });

                    final Player player1 = plugin.getServer().getPlayer(player1name);

                    if (player1 != null)
                    {
                        player1.sendMessage(player1message);
                        CommandAPI.updateRequirements(player1);
                    }
                })
                .register();
    }
}
