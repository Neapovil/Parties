package com.github.nearata.parties;

import java.io.File;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.nearata.parties.command.AcceptCommand;
import com.github.nearata.parties.command.ChatCommand;
import com.github.nearata.parties.command.CreateCommand;
import com.github.nearata.parties.command.DisbandCommand;
import com.github.nearata.parties.command.GotoCommand;
import com.github.nearata.parties.command.InviteCommand;
import com.github.nearata.parties.command.KickCommand;
import com.github.nearata.parties.command.LeaveCommand;
import com.github.nearata.parties.command.ListCommand;
import com.github.nearata.parties.command.PromoteCommand;
import com.github.nearata.parties.listener.Listener;
import com.github.nearata.parties.manager.Manager;
import com.github.nearata.parties.runnable.GotoRunnable;
import com.github.nearata.parties.runnable.PartyInviteRunnable;

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
