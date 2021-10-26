package com.github.nearata.parties;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.nearata.parties.listener.PartiesListener;
import com.github.nearata.parties.manager.PartiesManager;
import com.github.nearata.parties.object.PartyInvite;
import com.github.nearata.parties.runnable.PartyInviteRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.md_5.bungee.api.ChatColor;

public final class Parties extends JavaPlugin
{
    private static Parties instance;
    private FileConfig partiesConfig;
    private FileConfig messagesConfig;
    private PartiesManager partiesManager;

    @Override
    public void onEnable()
    {
        instance = this;
        
        this.saveResource("parties.json", false);
        this.saveResource("messages.toml", false);

        this.partiesConfig = FileConfig.builder(new File(this.getDataFolder(), "parties.json"))
                .autoreload()
                .autosave()
                .build();
        this.partiesConfig.load();

        this.messagesConfig = FileConfig.builder(new File(this.getDataFolder(), "messages.toml"))
                .autoreload()
                .autosave()
                .build();
        this.messagesConfig.load();

        this.partiesManager = new PartiesManager();

        this.getServer().getPluginManager().registerEvents(new PartiesListener(), this);

        new PartyInviteRunnable().runTaskTimer(this, 0, 20);

        new CommandAPICommand("party")
                .withPermission("parties.command.create")
                .withArguments(new LiteralArgument("create"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    final NamespacedKey key = new NamespacedKey(this, "party");

                    if (player.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.has_party"));
                    }

                    final String partyid = StringUtils.left(UUID.randomUUID().toString().replace("-", ""), 16);
                    player.getPersistentDataContainer().set(key, PersistentDataType.STRING, StringUtils.left(partyid, 16));

                    final String path = "parties." + partyid;
                    this.partiesConfig.set(path + ".leader", uuid.toString());
                    this.partiesConfig.set(path + ".players", List.of(uuid.toString()));

                    final Team team = player.getScoreboard().registerNewTeam(partyid);
                    team.addEntry(player.getName());

                    player.sendMessage(ChatColor.GREEN + (String) this.messagesConfig.get("info.party_created"));
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    final NamespacedKey key = new NamespacedKey(this, "party");

                    if (!player.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.no_party"));
                    }

                    final String partyid = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);

                    player.getPersistentDataContainer().remove(key);
                    player.getScoreboard().getTeam(partyid).unregister();

                    for (String ppuuid : this.partiesManager.getPartyPlayers(partyid))
                    {
                        final Player partyplayer = this.getServer().getPlayer(UUID.fromString(ppuuid));

                        if (partyplayer == null)
                        {
                            continue;
                        }

                        if (partyplayer.getUniqueId().equals(uuid))
                        {
                            continue;
                        }

                        partyplayer.getScoreboard().getTeam(partyid).unregister();
                        partyplayer.getPersistentDataContainer().remove(key);
                        partyplayer.sendMessage(ChatColor.RED + (String) this.messagesConfig.get("info.party_disbanded_by"));
                    }

                    this.partiesConfig.remove("parties." + partyid);

                    player.sendMessage(ChatColor.RED + (String) this.messagesConfig.get("info.party_disbanded"));
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.list")
                .withArguments(new LiteralArgument("list"))
                .executesPlayer((player, args) -> {
                    final NamespacedKey key = new NamespacedKey(this, "party");

                    if (!player.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.no_party"));
                    }

                    final Team team = player.getScoreboard().getTeam(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));

                    player.sendMessage("Party Members: " + String.join(", ", team.getEntries()));
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.invite")
                .withArguments(new LiteralArgument("invite"))
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    final NamespacedKey key = new NamespacedKey(this, "party");

                    if (!player.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.no_party"));
                    }

                    final Player player1 = (Player) args[0];
                    final UUID uuid1 = player1.getUniqueId();

                    if (uuid.equals(uuid1))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.cannot_self_invite"));
                    }

                    final String partyid = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);

                    if (this.partiesManager.getInvites().get(partyid).stream().anyMatch(i -> i.getUUID().equals(uuid1)))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.already_invited"));
                    }

                    this.partiesManager.getInvites().put(partyid, new PartyInvite(player.getName(), uuid1));
                    player1.sendMessage(((String) this.messagesConfig.get("info.invited_by")).formatted(player.getName()));
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.accept")
                .withArguments(new LiteralArgument("accept"))
                .withArguments(new StringArgument("player").replaceSuggestions(s -> {
                    return this.partiesManager.getInvites()
                            .values()
                            .stream()
                            .filter(i -> i.getUUID().equals(((Player) s.sender()).getUniqueId()))
                            .map(i -> i.getIssuer())
                            .toArray(String[]::new);
                }))
                .executesPlayer((player, args) -> {
                    final NamespacedKey key = new NamespacedKey(this, "party");

                    if (player.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.has_party"));
                    }

                    final String issuer = (String) args[0];
                    String partyid = null;

                    for (Entry<String, PartyInvite> i : this.partiesManager.getInvites().entries())
                    {
                        if (i.getValue().getIssuer().equals(issuer) && i.getValue().getUUID().equals(player.getUniqueId()))
                        {
                            partyid = i.getKey();
                            break;
                        }
                    }

                    if (partyid == null)
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.invalid_invite"));
                    }

                    for (String ppid : this.partiesManager.getPartyPlayers(partyid))
                    {
                        final Player partyplayer = this.getServer().getPlayer(ppid);

                        if (partyplayer == null)
                        {
                            continue;
                        }

                        partyplayer.sendMessage(ChatColor.GREEN + ((String) this.messagesConfig.get("info.player_joined")).formatted(player.getName()));
                    }

                    player.getScoreboard().getTeam(partyid).addEntry(player.getName());
                    this.partiesManager.getInvites().values().removeIf(i -> i.getIssuer().equals(issuer) && i.getUUID().equals(player.getUniqueId()));

                    player.sendMessage(ChatColor.GREEN + (String) this.messagesConfig.get("info.party_joined"));
                })
                .register();
    }

    @Override
    public void onDisable()
    {
    }

    public static Parties getInstance()
    {
        return instance;
    }

    public FileConfig getPartiesConfig()
    {
        return this.partiesConfig;
    }

    public FileConfig getMessagesConfig()
    {
        return this.messagesConfig;
    }

    public PartiesManager getPartiesManager()
    {
        return this.partiesManager;
    }
}
