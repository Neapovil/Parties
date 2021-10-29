package com.github.nearata.parties.util;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.nearata.parties.Parties;

public final class Util
{
    private static final Parties plugin = Parties.getInstance();

    public static Set<Player> getOnlineMembers(Set<String> entries, UUID leaderId)
    {
        return entries.stream()
                .map(username -> plugin.getServer().getPlayer(username))
                .filter(player -> player != null)
                .filter(player -> !player.getName().startsWith("leader-"))
                .filter(player -> {
                    if (leaderId == null)
                    {
                        return true;
                    }

                    return !player.getUniqueId().equals(leaderId);
                })
                .collect(Collectors.toSet());
    }

    public static Optional<Team> getParty(Player player)
    {
        final String partyid = player.getPersistentDataContainer().get(plugin.getKey(), plugin.getKeyType());

        if (partyid == null)
        {
            return Optional.empty();
        }

        final Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

        if (team == null)
        {
            return Optional.empty();
        }

        if (!team.getEntries().contains(player.getName()))
        {
            return Optional.empty();
        }

        return Optional.ofNullable(team);
    }
}
