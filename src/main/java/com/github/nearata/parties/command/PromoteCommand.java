package com.github.nearata.parties.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class PromoteCommand
{
    private static final Parties plugin = Parties.getInstance();
    private static final List<String> ranks = new ArrayList<>(List.of("leader", "mod"));

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.promote")
                .withArguments(new MultiLiteralArgument("promote", "demote"))
                .withArguments(new StringArgument("player").replaceSuggestions(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getMembers(player).stream().filter(s -> !s.startsWith(player.getName())).toArray(String[]::new);
                }))
                .withArguments(new StringArgument("rank").replaceSuggestions(info -> {
                    if (info.previousArgs()[0].equals("promote"))
                    {
                        return ranks.toArray(String[]::new);
                    }

                    return new String[] { "member" };
                }))
                .executesPlayer((player, args) -> {
                    final String command = (String) args[0];
                    final String player1name = (String) args[1];
                    final String rank = (String) args[2];

                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_NO_PERMISSIONS_ONLY_LEADER.get()));
                    }

                    if (player.getName().equals(player1name))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_PROMOTE_SELF.get()));
                    }

                    if (!team.getEntries().contains(player1name))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_PLAYER_NOT_FOUND.get()));
                    }

                    final Player player1 = plugin.getServer().getPlayer(player1name);
                    String player1msg;
                    String partymsg;
                    String sendermsg;

                    if (command.equals("promote"))
                    {
                        if (!ranks.contains(rank))
                        {
                            CommandAPI.fail(plugin.getMessage(Messages.SENDER_PARTY_RANK_NOT_FOUND.get()));
                        }

                        if (team.getEntries().contains(rank + "-" + player1name))
                        {
                            CommandAPI.fail(plugin.getMessage(Messages.SENDER_PLAYER_ALREADY_MOD.get()));
                        }

                        if (rank.equals("leader"))
                        {
                            team.removeEntry("leader-" + player.getName());

                            if (team.getEntries().contains("mod-" + player1name))
                            {
                                team.removeEntry("mod-" + player1name);
                            }
                        }

                        team.addEntry(rank + "-" + player1name);
                        player1msg = plugin.getMessage(Messages.PLAYER_PROMOTED.get()).formatted(rank);
                        partymsg = plugin.getMessage(Messages.PARTY_PROMOTED.get()).formatted(player1name, rank);
                        sendermsg = plugin.getMessage(Messages.SENDER_PROMOTED.get()).formatted(player1name, rank);
                    }
                    else
                    {
                        if (rank.equals("leader"))
                        {
                            CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_DEMOTE_LEADER.get()));
                        }

                        if (!team.getEntries().contains("mod-" + player1name))
                        {
                            CommandAPI.fail(plugin.getMessage(Messages.SENDER_PLAYER_NOT_MOD.get()));
                        }

                        team.removeEntry("mod-" + player1name);
                        player1msg = plugin.getMessage(Messages.PLAYER_DEMOTED.get());
                        partymsg = plugin.getMessage(Messages.PARTY_DEMOTED.get()).formatted(player1name);
                        sendermsg = plugin.getMessage(Messages.SENDER_DEMOTED.get()).formatted(player1name);
                    }

                    if (player1 != null)
                    {
                        player1.sendMessage(player1msg);
                    }

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        if (p.getName().equals(player1name))
                        {
                            return;
                        }

                        p.sendMessage(partymsg);
                    });

                    player.sendMessage(sendermsg);
                })
                .register();
    }
}
