package com.github.neapovil.parties;

import java.io.File;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.electronwill.nightconfig.core.file.FileConfig;
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
    private FileConfig messages;
    private Manager manager;

    @Override
    public void onEnable()
    {
        instance = this;

        this.saveResource("messages.toml", false);
        this.messages = FileConfig.builder(new File(this.getDataFolder(), "messages.toml"))
                .autoreload()
                .autosave()
                .build();
        this.messages.load();

        this.manager = new Manager();

        this.getServer().getPluginManager().registerEvents(new Listener(), this);

        new PartyInviteRunnable().runTaskTimer(this, 0, 20);
        new GotoRunnable().runTaskTimer(this, 0, 20);

        CreateCommand.register();
        DisbandCommand.register();
        ListCommand.register();
        InviteCommand.register();
        AcceptCommand.register();
        LeaveCommand.register();
        KickCommand.register();
        ChatCommand.register();
        PromoteCommand.register();
        GotoCommand.register();
        ModifyCommand.register();
    }

    @Override
    public void onDisable()
    {
    }

    public static Parties getInstance()
    {
        return instance;
    }

    public String getMessage(String path)
    {
        return (String) this.messages.get(path);
    }

    public Manager getManager()
    {
        return this.manager;
    }

    public NamespacedKey getKey()
    {
        return new NamespacedKey(this, "party");
    }

    public PersistentDataType<String, String> getKeyType()
    {
        return PersistentDataType.STRING;
    }
}
