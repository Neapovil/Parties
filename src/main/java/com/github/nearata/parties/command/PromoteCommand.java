package com.github.nearata.parties.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.message.MessageError;
import com.github.nearata.parties.message.MessageInfo;
import com.github.nearata.parties.util.Util;

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
                .withPermission("party.command.promote")
                .withArguments(new MultiLiteralArgument("promote", "demote"))
                .withArguments(new StringArgument("player").replaceSuggestions(info -> {
                    return Util.getMembers((Player) info.sender(), true).toArray(String[]::new);
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
                    final String playername = (String) args[1];
                    final String rank = (String) args[2];

                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();

                    if (!team.getEntries().contains("leader-" + player.getName()))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.NO_PERMISSIONS_ONLY_LEADER.get()));
                    }

                    if (player.getName().equals(playername))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.CANNOT_SELF_PROMOTE.get()));
                    }

                    if (!team.getEntries().contains(playername))
                    {
                        CommandAPI.fail(plugin.getMessage(MessageError.PLAYER_NOT_FOUND.get()));
                    }

                    final Player player1 = plugin.getServer().getPlayer(playername);
                    String player1msg;
                    String partymsg;
                    String sendermsg;

                    if (command.equals("promote"))
                    {
                        if (!ranks.contains(rank))
                        {
                            CommandAPI.fail(plugin.getMessage(MessageError.PARTY_RANK_NOT_FOUND.get()));
                        }

                        if (team.getEntries().contains(rank + "-" + playername))
                        {
                            CommandAPI.fail(plugin.getMessage(MessageError.PLAYER_ALREADY_MOD.get()));
                        }

                        if (rank.equals("leader"))
                        {
                            team.removeEntry("leader-" + player.getName());

                            if (team.getEntries().contains("mod-" + playername))
                            {
                                team.removeEntry("mod-" + playername);
                            }
                        }

                        team.addEntry(rank + "-" + playername);
                        player1msg = plugin.getMessage(MessageInfo.PLAYER_PROMOTED.get()).formatted(rank);
                        partymsg = plugin.getMessage(MessageInfo.PARTY_PROMOTED.get()).formatted(playername, rank);
                        sendermsg = plugin.getMessage(MessageInfo.SENDER_PROMOTED.get()).formatted(playername, rank);
                    }
                    else
                    {
                        if (rank.equals("leader"))
                        {
                            CommandAPI.fail(plugin.getMessage(MessageError.CANNOT_DEMOTE_LEADER.get()));
                        }

                        if (!team.getEntries().contains("mod-" + playername))
                        {
                            CommandAPI.fail(plugin.getMessage(MessageError.PLAYER_NOT_MOD.get()));
                        }

                        team.removeEntry("mod-" + playername);
                        player1msg = plugin.getMessage(MessageInfo.PLAYER_DEMOTED.get());
                        partymsg = plugin.getMessage(MessageInfo.PARTY_DEMOTED.get()).formatted(playername);
                        sendermsg = plugin.getMessage(MessageInfo.SENDER_DEMOTED.get()).formatted(playername);
                    }

                    if (player1 != null)
                    {
                        player1.sendMessage(player1msg);
                    }

                    Util.getOnlineMembers(player, true).forEach(p -> {
                        if (p.getName().equals(playername))
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
