package com.github.nearata.parties;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.electronwill.nightconfig.core.file.FileConfig;
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

                    final String partyid = StringUtils.left(uuid.toString().replace("-", ""), 16);
                    player.getPersistentDataContainer().set(key, PersistentDataType.STRING, partyid);

                    final Team team = this.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(partyid);
                    team.addEntry(player.getName());
                    team.setAllowFriendlyFire(false);

                    player.sendMessage(ChatColor.GREEN + (String) this.messagesConfig.get("info.party_created"));
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.disband")
                .withArguments(new LiteralArgument("disband"))
                .executesPlayer((player, args) -> {
                    final UUID uuid = player.getUniqueId();
                    final NamespacedKey key = new NamespacedKey(this, "party");

                    final PersistentDataContainer data = player.getPersistentDataContainer();

                    if (!data.has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.no_party"));
                    }

                    final String partyid = data.get(key, PersistentDataType.STRING);

                    data.remove(key);
                    final Team team = this.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    for (String username : team.getEntries())
                    {
                        final Player partyplayer = this.getServer().getPlayer(username);

                        if (partyplayer == null)
                        {
                            continue;
                        }

                        if (partyplayer.getUniqueId().equals(uuid))
                        {
                            continue;
                        }

                        partyplayer.getPersistentDataContainer().remove(key);
                        partyplayer.sendMessage(ChatColor.RED + (String) this.messagesConfig.get("info.party_disbanded_by"));
                    }

                    team.unregister();
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

                    final Team team = this.getServer()
                            .getScoreboardManager()
                            .getMainScoreboard()
                            .getTeam(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));

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

                    if (player1.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.invited_player_has_party"));
                    }

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

                    player.sendMessage(((String) this.messagesConfig.get("info.invited")).formatted(player1.getName()));
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

                    final Optional<String> partyid = this.partiesManager.getInvites()
                            .entries()
                            .stream()
                            .filter(e -> {
                                return e.getValue().getIssuer().equals(issuer)
                                        && e.getValue().getUUID().equals(player.getUniqueId());
                            })
                            .map(e -> e.getKey())
                            .findAny();

                    if (partyid.isEmpty())
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.expired_invite"));
                    }

                    final Team team = this.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid.get());

                    for (String username : team.getEntries())
                    {
                        final Player partyplayer = this.getServer().getPlayer(username);

                        if (partyplayer == null)
                        {
                            continue;
                        }

                        partyplayer.sendMessage(ChatColor.GREEN + ((String) this.messagesConfig.get("info.player_joined")).formatted(player.getName()));
                    }

                    team.addEntry(player.getName());
                    player.getPersistentDataContainer().set(key, PersistentDataType.STRING, partyid.get());
                    this.partiesManager.getInvites().values().removeIf(i -> i.getIssuer().equals(issuer) && i.getUUID().equals(player.getUniqueId()));

                    player.sendMessage(ChatColor.GREEN + (String) this.messagesConfig.get("info.party_joined"));
                })
                .register();

        new CommandAPICommand("party")
                .withPermission("parties.command.leave")
                .withArguments(new LiteralArgument("leave"))
                .executesPlayer((player, args) -> {
                    final NamespacedKey partykey = new NamespacedKey(this, "party");

                    final PersistentDataContainer data = player.getPersistentDataContainer();

                    if (!data.has(partykey, PersistentDataType.STRING))
                    {
                        CommandAPI.fail(this.messagesConfig.get("errors.no_party"));
                    }

                    final String partyid = data.get(partykey, PersistentDataType.STRING);
                    final Team team = this.getServer().getScoreboardManager().getMainScoreboard().getTeam(partyid);

                    data.remove(partykey);
                    team.removeEntry(player.getName());
                    player.sendMessage((String) this.messagesConfig.get("info.party_left"));
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

    public FileConfig getMessagesConfig()
    {
        return this.messagesConfig;
    }

    public PartiesManager getPartiesManager()
    {
        return this.partiesManager;
    }
}
