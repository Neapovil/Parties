package com.github.neapovil.parties.command;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.neapovil.parties.Parties;
import com.github.neapovil.parties.object.PartyInvite;
import com.github.neapovil.parties.util.Util;
import com.github.neapovil.parties.util.Util.PartyRank;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;

public final class InviteCommand implements ICommand
{
    private final Parties plugin = Parties.instance();

    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.invite")
                .withArguments(new LiteralArgument("invite").withRequirement(sender -> {
                    final PartyRank partyrank = Util.getRank((Player) sender);
                    return partyrank.equals(PartyRank.LEADER) || partyrank.equals(PartyRank.MOD);
                }))
                .withArguments(new PlayerArgument("player").replaceSafeSuggestions(SafeSuggestions.suggest(info -> {
                    final Player player = (Player) info.sender();
                    final Team team = Util.getParty(player).get();
                    final Predicate<Player> predicateinvited = (i) -> {
                        return plugin.getManager()
                                .getInvites()
                                .get(team.getName())
                                .stream()
                                .noneMatch(i1 -> i1.getUUID().equals(i.getUniqueId()));
                    };
                    return Stream.of(plugin.getServer().getOnlinePlayers().toArray(Player[]::new))
                            .filter(i -> !i.getUniqueId().equals(player.getUniqueId()))
                            .filter(i -> Util.getParty(i).isEmpty())
                            .filter(predicateinvited)
                            .toArray(Player[]::new);
                })))
                .executesPlayer((player, args) -> {
                    final Player player1 = (Player) args.get("player");

                    final Team team = Util.getParty(player).get();
                    final UUID uuid1 = player1.getUniqueId();

                    if (player.getUniqueId().equals(uuid1))
                    {
                        return;
                    }

                    if (Util.getParty(player1).isPresent())
                    {
                        return;
                    }

                    if (plugin.getManager().getInvites().get(team.getName()).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        return;
                    }

                    plugin.getManager().getInvites().put(team.getName(), new PartyInvite(player.getName(), uuid1));

                    player.sendMessage("You invited %s to join your party".formatted(player1.getName()));
                    player1.sendMessage("%s invited you to join their party".formatted(player.getName()));
                })
                .register();
    }
}
