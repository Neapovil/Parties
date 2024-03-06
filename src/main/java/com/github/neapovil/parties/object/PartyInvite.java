package com.github.neapovil.parties.object;

import java.time.Instant;

import com.github.neapovil.parties.resource.PartiesResource;

public class PartyInvite
{
    public final PartiesResource.Party party;
    public final String leaderName;
    public final Instant expire = Instant.now().plusSeconds(30);

    public PartyInvite(PartiesResource.Party party, String leaderName)
    {
        this.party = party;
        this.leaderName = leaderName;
    }
}
