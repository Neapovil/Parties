package com.github.nearata.parties.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

public final class Util
{
    @Nullable
    public static Player getPlayerFromEntity(Entity entity)
    {
        if (entity instanceof Player)
        {
            return (Player) entity;
        }

        if (entity instanceof Projectile)
        {
            final Projectile projectile = (Projectile) entity;
            final ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof Player)
            {
                return (Player) shooter;
            }
        }

        return null;
    }
}
