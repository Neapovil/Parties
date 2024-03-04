package com.github.neapovil.parties.runnable;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.neapovil.parties.Parties;

import net.kyori.adventure.text.Component;

public final class GotoRunnable extends BukkitRunnable
{
    private final Parties plugin = Parties.instance();

    @Override
    public void run()
    {
        plugin.getManager().getGoto().entrySet().removeIf(e -> {
            final Player player = plugin.getServer().getPlayer(e.getKey());
            final Player player1 = plugin.getServer().getPlayer(e.getValue());

            final boolean check = player == null || player1 == null;

            if (check)
            {
                return true;
            }

            final String world = player.getWorld().getName();
            final String world1 = player1.getWorld().getName();
            final boolean check1 = !world.equals(world1);

            if (check1)
            {
                return true;
            }

            if (player.getLocation().distanceSquared(player1.getLocation()) <= 120)
            {
                return true;
            }

            return false;
        });

        plugin.getManager().getGoto().forEach((id, id1) -> {
            final Player player = plugin.getServer().getPlayer(id);
            final Player player1 = plugin.getServer().getPlayer(id1);
            final int distance = (int) player.getLocation().distanceSquared(player1.getLocation());
            final String message = "Player: %s Distance: %s".formatted(player1.getName(), distance);

            player.sendActionBar(Component.text(message));
        });
    }
}
