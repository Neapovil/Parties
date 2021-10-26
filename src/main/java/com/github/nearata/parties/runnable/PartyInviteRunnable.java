package com.github.nearata.parties.runnable;

import java.time.Instant;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.nearata.parties.Parties;

public final class PartyInviteRunnable extends BukkitRunnable
{
    private final Parties plugin = Parties.getInstance();

    @Override
    public void run()
    {
        plugin.getPartiesManager().getInvites().values().removeIf(i -> Instant.now().isAfter(i.getTime()));
    }
}
