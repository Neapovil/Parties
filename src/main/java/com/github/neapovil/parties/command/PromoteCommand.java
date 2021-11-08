package com.github.neapovil.parties.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.messages.Messages;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

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
                        Messages.NO_PARTY.fail();
                    }

                    final Team team = Util.getParty(player).get();

                    if (!Util.getRank(player).equals(PartyRank.LEADER))
                    {
                        Messages.SENDER_NO_PERMISSIONS_ONLY_LEADER.fail();
                    }

                    if (player.getName().equals(player1name))
                    {
                        Messages.SENDER_CANNOT_PROMOTE_SELF.fail();
                    }

                    if (!team.getEntries().contains(player1name))
                    {
                        Messages.SENDER_PLAYER_NOT_FOUND.fail();
                    }

                    final Player player1 = plugin.getServer().getPlayer(player1name);
                    Messages player1msg;
                    List<String> player1obj;

                    Messages partymsg;
                    List<String> partyobj;

                    Messages sendermsg;
                    List<String> senderobj;

                    if (command.equals("promote"))
                    {
                        if (!ranks.contains(rank))
                        {
                            Messages.SENDER_PARTY_RANK_NOT_FOUND.fail();
                        }

                        if (team.getEntries().contains(rank + "-" + player1name))
                        {
                            Messages.SENDER_PLAYER_ALREADY_MOD.fail();
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

                        player1msg = Messages.PLAYER_PROMOTED;
                        player1obj = List.of(rank);

                        partymsg = Messages.PARTY_PROMOTED;
                        partyobj = List.of(player1name, rank);

                        sendermsg = Messages.SENDER_PROMOTED;
                        senderobj = List.of(player1name, rank);
                    }
                    else
                    {
                        if (rank.equals("leader"))
                        {
                            Messages.SENDER_CANNOT_DEMOTE_LEADER.fail();
                        }

                        if (!team.getEntries().contains("mod-" + player1name))
                        {
                            Messages.SENDER_PLAYER_NOT_MOD.fail();
                        }

                        team.removeEntry("mod-" + player1name);

                        player1msg = Messages.PLAYER_DEMOTED;
                        player1obj = List.of();

                        partymsg = Messages.PARTY_DEMOTED;
                        partyobj = List.of(player1name);

                        sendermsg = Messages.SENDER_DEMOTED;
                        senderobj = List.of(player1name);
                    }

                    if (player1 != null)
                    {
                        player1msg.send(player1, player1obj.toArray());
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

                        partymsg.send(p, partyobj.toArray());
                    });

                    sendermsg.send(player, senderobj.toArray());
                })
                .register();
    }
}
