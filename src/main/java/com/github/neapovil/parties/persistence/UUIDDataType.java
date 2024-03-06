package com.github.neapovil.parties.persistence;

import java.util.UUID;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class UUIDDataType implements PersistentDataType<String, UUID>
{
    @Override
    public @NotNull Class<String> getPrimitiveType()
    {
        return String.class;
    }

    @Override
    public @NotNull Class<UUID> getComplexType()
    {
        return UUID.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context)
    {
        return complex.toString();
    }

    @Override
    public @NotNull UUID fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context)
    {
        return UUID.fromString(primitive);
    }

}
