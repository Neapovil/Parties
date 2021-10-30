package com.github.nearata.parties.message;

public enum MessageError
{
    NO_PARTY("no_party"),
    HAS_PARTY("has_party"),
    CANNOT_SELF_INVITE("cannot_self_invite"),
    ALREADY_INVITED("already_invited"),
    EXPIRED_INVITE("expired_invite"),
    INVITED_PLAYER_HAS_PARTY("invited_player_has_party"),
    CANNOT_DISBAND_NOT_LEADER("cannot_disband_not_leader"),
    CANNOT_LEAVE_LEADER("cannot_leave_leader"),
    NO_PERMISSIONS("no_permissions"),
    CANNOT_KICK_NOT_IN_PARTY("cannot_kick_not_in_party"),
    PLAYER_NOT_FOUND("player_not_found"),
    PLAYER_ALREADY_MOD("player_already_mod"),
    NO_PERMISSIONS_ONLY_LEADER("no_permissions_only_leader"),
    PARTY_RANK_NOT_FOUND("party_rank_not_found"),
    CANNOT_SELF_PROMOTE("cannot_self_promote"),
    PLAYER_NOT_MOD("player_not_mod"),
    CANNOT_DEMOTE_LEADER("cannot_demote_leader");

    private final String message;

    MessageError(String message)
    {
        this.message = message;
    }

    public String get()
    {
        return "errors." + this.message;
    }
}
