package com.github.neapovil.parties;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.neapovil.parties.command.AcceptCommand;
import com.github.neapovil.parties.command.ChatCommand;
import com.github.neapovil.parties.command.CreateCommand;
import com.github.neapovil.parties.command.DisbandCommand;
import com.github.neapovil.parties.command.GotoCommand;
import com.github.neapovil.parties.command.InviteCommand;
import com.github.neapovil.parties.command.KickCommand;
import com.github.neapovil.parties.command.LeaveCommand;
import com.github.neapovil.parties.command.ListCommand;
import com.github.neapovil.parties.command.ModifyCommand;
import com.github.neapovil.parties.command.PromoteCommand;
import com.github.neapovil.parties.listener.Listener;
import com.github.neapovil.parties.manager.Manager;
import com.github.neapovil.parties.runnable.GotoRunnable;
import com.github.neapovil.parties.runnable.PartyInviteRunnable;

public final class Parties extends JavaPlugin
{
    private static Parties instance;
    private Manager manager;
    public final NamespacedKey partyIdKey = new NamespacedKey(this, "party-id");

    @Override
    public void onEnable()
    {
        instance = this;

        this.manager = new Manager();

        this.getServer().getPluginManager().registerEvents(new Listener(), this);

        new PartyInviteRunnable().runTaskTimer(this, 0, 20);
        new GotoRunnable().runTaskTimer(this, 0, 20);

        new AcceptCommand().register();
        new ChatCommand().register();
        new CreateCommand().register();
        new DisbandCommand().register();
        new GotoCommand().register();
        new InviteCommand().register();
        new KickCommand().register();
        new LeaveCommand().register();
        new ListCommand().register();
        new ModifyCommand().register();
        new PromoteCommand().register();
    }

    @Override
    public void onDisable()
    {
    }

    public static Parties instance()
    {
        return instance;
    }

    public Manager getManager()
    {
        return this.manager;
    }
}
