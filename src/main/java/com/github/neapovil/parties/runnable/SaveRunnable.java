package com.github.neapovil.parties.runnable;

import java.io.IOException;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.neapovil.parties.Parties;

public final class SaveRunnable extends BukkitRunnable
{
    private final Parties plugin = Parties.instance();

    @Override
    public void run()
    {
        try
        {
            plugin.save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
