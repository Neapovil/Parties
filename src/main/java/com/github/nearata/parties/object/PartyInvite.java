package com.github.nearata.parties.object;

import java.time.Instant;
import java.util.UUID;

public final class PartyInvite
{
    private final String issuer;
    private final UUID uuid;
    private final Instant time;

    public PartyInvite(String issuer, UUID uuid)
    {
        this.issuer = issuer;
        this.uuid = uuid;
        this.time = Instant.now().plusSeconds(30);
    }

    public String getIssuer()
    {
        return this.issuer;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public Instant getTime()
    {
        return this.time;
    }
}
