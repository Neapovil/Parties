package com.github.neapovil.parties.command;

import com.github.neapovil.parties.Parties;

public abstract class AbstractCommand
{
    protected Parties plugin = Parties.instance();

    abstract void register();
}
