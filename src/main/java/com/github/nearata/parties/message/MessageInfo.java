package com.github.nearata.parties.message;

public enum MessageInfo
{
    PARTY_CREATED("party_created"), INVITED_BY("invited_by"), PLAYER_JOINED("player_joined"), PARTY_JOINED("party_joined"), PARTY_DISBANDED_BY(
            "party_disbanded_by"), PARTY_DISBANDED("party_disbanded"), INVITED(
                    "invited"), PARTY_LEFT(
                            "party_left"), PLAYER_LEFT("player_left"), PARTY_KICKED("party_kicked"), PLAYER_KICKED("player_kicked"), YOU_KICKED("you_kicked");

    private final String message;

    MessageInfo(String message)
    {
        this.message = message;
    }

    public String get()
    {
        return "info." + this.message;
    }
}
