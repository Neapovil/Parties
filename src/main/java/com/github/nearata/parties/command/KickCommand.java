package com.github.nearata.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class KickCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.kick")
                .withArguments(new LiteralArgument("kick"))
                .withArguments(new StringArgument("player").replaceSuggestions(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getMembers(player).stream().filter(s -> !s.startsWith(player.getName())).toArray(String[]::new);
                }))
                .executesPlayer((player, args) -> {
                    final String playername = (String) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        Messages.NO_PARTY.fail();
                    }

                    final Team team = Util.getParty(player).get();

                    if (!(Util.getRank(player).equals(PartyRank.MOD) || Util.getRank(player).equals(PartyRank.LEADER)))
                    {
                        Messages.SENDER_NO_PERMISSIONS.fail();
                    }

                    if (playername.equals(player.getName()))
                    {
                        Messages.SENDER_CANNOT_KICK_SELF.fail();
                    }

                    if (!team.getEntries().contains(playername))
                    {
                        Messages.SENDER_PLAYER_NOT_IN_PARTY.fail();
                    }

                    if (!Util.getRank(player).equals(PartyRank.LEADER) && team.getEntries().contains("mod-" + playername))
                    {
                        Messages.SENDER_CANNOT_KICK_MOD.fail();
                    }

                    if (team.getEntries().contains("leader-" + playername))
                    {
                        Messages.SENDER_CANNOT_KICK_LEADER.fail();
                    }

                    team.removeEntry(playername);

                    final Player player1 = plugin.getServer().getPlayer(playername);

                    if (player1 != null)
                    {
                        player1.getPersistentDataContainer().remove(plugin.getKey());
                        Messages.PLAYER_KICKED.send(player1);
                    }

                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        Messages.PARTY_KICKED.send(p, player1.getName());
                    });

                    Messages.SENDER_KICKED.send(player, player1.getName());
                })
                .register();
    }
}
