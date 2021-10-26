package com.github.nearata.parties.manager;

import com.github.nearata.parties.object.PartyInvite;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public final class PartiesManager
{
    private final Multimap<String, PartyInvite> invites = ArrayListMultimap.create();

    public Multimap<String, PartyInvite> getInvites()
    {
        return this.invites;
    }
}
