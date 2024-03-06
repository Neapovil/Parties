package com.github.neapovil.parties.runnable;

import java.time.Instant;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.neapovil.parties.Parties;

public final class PartyInviteRunnable extends BukkitRunnable
{
    private final Parties plugin = Parties.instance();

    @Override
    public void run()
    {
        plugin.invites.values().removeIf(i -> Instant.now().isAfter(i.expire));
    }
}
