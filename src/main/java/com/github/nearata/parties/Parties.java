package com.github.nearata.parties;

import java.io.File;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.nearata.parties.command.AcceptCommand;
import com.github.nearata.parties.command.CreateCommand;
import com.github.nearata.parties.command.DisbandCommand;
import com.github.nearata.parties.command.InviteCommand;
import com.github.nearata.parties.command.LeaveCommand;
import com.github.nearata.parties.command.ListCommand;
import com.github.nearata.parties.manager.PartiesManager;
import com.github.nearata.parties.runnable.PartyInviteRunnable;

public final class Parties extends JavaPlugin
{
    private static Parties instance;
    private FileConfig messagesConfig;
    private PartiesManager partiesManager;

    @Override
    public void onEnable()
    {
        instance = this;

        this.saveResource("messages.toml", false);
        this.messagesConfig = FileConfig.builder(new File(this.getDataFolder(), "messages.toml"))
                .autoreload()
                .autosave()
                .build();
        this.messagesConfig.load();

        this.partiesManager = new PartiesManager();

        new PartyInviteRunnable().runTaskTimer(this, 0, 20);

        CreateCommand.register();
        DisbandCommand.register();
        ListCommand.register();
        InviteCommand.register();
        AcceptCommand.register();
        LeaveCommand.register();
    }

    @Override
    public void onDisable()
    {
    }

    public static Parties getInstance()
    {
        return instance;
    }

    public FileConfig getMessagesConfig()
    {
        return this.messagesConfig;
    }

    public PartiesManager getPartiesManager()
    {
        return this.partiesManager;
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
