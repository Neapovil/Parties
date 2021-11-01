package com.github.nearata.parties.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.nearata.parties.object.PartyInvite;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public final class Manager
{
    private final Multimap<String, PartyInvite> invites = ArrayListMultimap.create();
    private final Map<UUID, UUID> pgoto = new HashMap<>();

    public Multimap<String, PartyInvite> getInvites()
    {
        return this.invites;
    }

    public Map<UUID, UUID> getGoto()
    {
        return this.pgoto;
    }
}
