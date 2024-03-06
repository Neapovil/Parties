package com.github.neapovil.parties;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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
import com.github.neapovil.parties.object.PartyInvite;
import com.github.neapovil.parties.persistence.UUIDDataType;
import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.resource.PartiesResource.Party.Member.RoleAdapter;
import com.github.neapovil.parties.runnable.GotoRunnable;
import com.github.neapovil.parties.runnable.PartyInviteRunnable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Parties extends JavaPlugin
{
    private static Parties instance;
    public final NamespacedKey partyIdKey = new NamespacedKey(this, "party-id");
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(PartiesResource.Party.Member.Role.class, new RoleAdapter())
            .create();
    public PartiesResource partiesResource;
    public final Multimap<UUID, PartyInvite> invites = ArrayListMultimap.create();
    public final Map<UUID, UUID> partyGoto = new HashMap<>();
    public static final UUIDDataType UUID_DATA_TYPE = new UUIDDataType();

    @Override
    public void onEnable()
    {
        instance = this;

        this.saveResource("parties.json", false);

        try
        {
            this.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

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

    public void load() throws IOException
    {
        final String string = Files.readString(this.getDataFolder().toPath().resolve("parties.json"));
        this.partiesResource = this.gson.fromJson(string, PartiesResource.class);
    }

    public void save() throws IOException
    {
        final String string = this.gson.toJson(this.partiesResource);
        Files.write(this.getDataFolder().toPath().resolve("parties.json"), string.getBytes());
    }

    public Optional<PartiesResource.Party> findParty(Player player)
    {
        final UUID partyid = player.getPersistentDataContainer().get(this.partyIdKey, Parties.UUID_DATA_TYPE);
        return this.partiesResource.findParty(partyid);
    }

    public Optional<PartiesResource.Party.Member> findMember(Player player)
    {
        return this.findParty(player).map(party -> party.findMember(player)).orElse(Optional.empty());
    }
}
