package com.github.nearata.parties.message;

public enum MessageError
{
    NO_PARTY("no_party"), HAS_PARTY("has_party"), CANNOT_SELF_INVITE("cannot_self_invite"), ALREADY_INVITED("already_invited"), EXPIRED_INVITE(
            "expired_invite"), INVITED_PLAYER_HAS_PARTY("invited_player_has_party"), CANNOT_DISBAND_NOT_LEADER(
                    "cannot_disband_not_leader"), CANNOT_LEAVE_LEADER("cannot_leave_leader"), CANNOT_INVITE_NO_PERMISSIONS("cannot_invite_no_permissions");

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
