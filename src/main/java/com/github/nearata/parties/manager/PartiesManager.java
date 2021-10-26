package com.github.nearata.parties.manager;

import java.util.List;

import com.github.nearata.parties.Parties;
import com.github.nearata.parties.object.PartyInvite;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public final class PartiesManager
{
    private final Parties plugin = Parties.getInstance();
    private final Multimap<String, PartyInvite> invites = ArrayListMultimap.create();

    public List<String> getPartyPlayers(String partyId)
    {
        final List<String> players = plugin.getPartiesConfig().get("parties.%s.players".formatted(partyId));
        return players;
    }

    public Multimap<String, PartyInvite> getInvites()
    {
        return this.invites;
    }
}
