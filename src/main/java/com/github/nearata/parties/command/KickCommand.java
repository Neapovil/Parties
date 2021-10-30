package com.github.nearata.parties.command;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.messages.Messages;
import com.github.nearata.parties.util.Util;
import com.github.nearata.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class KickCommand
{
    private static final Parties plugin = Parties.getInstance();

    public static void register()
    {
        new CommandAPICommand("party")
                .withPermission("party.command.kick")
                .withArguments(new LiteralArgument("kick"))
                .withArguments(new StringArgument("player").replaceSuggestions(info -> {
                    final Player player = (Player) info.sender();
                    return Util.getMembers(player).stream().filter(s -> !s.startsWith(player.getName())).toArray(String[]::new);
                }))
                .executesPlayer((player, args) -> {
                    final String playername = (String) args[0];

                    if (Util.getParty(player).isEmpty())
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.NO_PARTY.get()));
                    }

                    final Team team = Util.getParty(player).get();

                    if (!(Util.getRank(player).equals(PartyRank.MOD) || Util.getRank(player).equals(PartyRank.LEADER)))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_NO_PERMISSIONS.get()));
                    }

                    if (playername.equals(player.getName()))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_KICK_SELF.get()));
                    }

                    if (!team.getEntries().contains(playername))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_PLAYER_NOT_IN_PARTY.get()));
                    }

                    if (!Util.getRank(player).equals(PartyRank.LEADER) && team.getEntries().contains("mod-" + playername))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_KICK_MOD.get()));
                    }

                    if (team.getEntries().contains("leader-" + playername))
                    {
                        CommandAPI.fail(plugin.getMessage(Messages.SENDER_CANNOT_KICK_LEADER.get()));
                    }

                    team.removeEntry(playername);

                    final Player player1 = plugin.getServer().getPlayer(playername);

                    if (player1 != null)
                    {
                        player1.getPersistentDataContainer().remove(plugin.getKey());
                        player1.sendMessage(plugin.getMessage(Messages.PLAYER_KICKED.get()));
                    }

                    final String msg = plugin.getMessage(Messages.PARTY_KICKED.get()).formatted(player1.getName());
                    Util.getOnlineMembers(player).forEach(p -> {
                        if (p.getName().equals(player.getName()))
                        {
                            return;
                        }

                        p.sendMessage(msg);
                    });

                    player.sendMessage(plugin.getMessage(Messages.SENDER_KICKED.get()).formatted(player1.getName()));
                })
                .register();
    }
}
