package com.github.neapovil.parties.resource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import com.github.neapovil.parties.Parties;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class PartiesResource
{
    public Optional<Party> findParty(UUID uuid)
    {
        return this.parties.stream().filter(i -> i.uuid.equals(uuid)).findFirst();
    }

    public static Party create(Player player)
    {
        final Party party = new Party(player.getUniqueId());
        party.members.add(new Party.Member(player.getName(), player.getUniqueId(), Party.Member.Role.LEADER_ROLE));

        player.getPersistentDataContainer().set(Parties.instance().partyIdKey, PersistentDataType.STRING, player.getUniqueId().toString());

        final Team team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getUniqueId().toString());
        team.addEntry(player.getName());
        team.setAllowFriendlyFire(false);

        return party;
    }

    public final List<Party> parties = new ArrayList<>();

    public static class Party
    {
        public UUID uuid;

        public Party(UUID uuid)
        {
            this.uuid = uuid;
        }

        public void add(Player player)
        {
            this.members.add(new Member(player.getName(), player.getUniqueId(), Member.Role.MEMBER_ROLE));
            this.team().addEntry(player.getName());
        }

        public Optional<Member> findMember(Player player)
        {
            return this.members.stream().filter(i -> i.uuid.equals(player.getUniqueId())).findFirst();
        }

        public Optional<Member> findMember(String username)
        {
            return this.members.stream().filter(i -> i.username.equalsIgnoreCase(username)).findFirst();
        }

        public Optional<Member> findMember(UUID uuid)
        {
            return this.members.stream().filter(i -> i.uuid.equals(uuid)).findFirst();
        }

        public Team team()
        {
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(this.uuid.toString());
        }

        public List<Player> onlineMembers()
        {
            return this.members.stream()
                    .map(i -> Bukkit.getPlayer(i.uuid))
                    .filter(i -> i != null)
                    .filter(i -> i.isOnline())
                    .toList();
        }

        public final List<Member> members = new ArrayList<>();

        public static class Member
        {
            public String username;
            public UUID uuid;

            public Member(String username, UUID uuid, Role role)
            {
                this.username = username;
                this.uuid = uuid;
                this.role = role;
            }

            public Role role;

            public static abstract class Role
            {
                public static final LeaderRole LEADER_ROLE = new LeaderRole();
                public static final ModRole MOD_ROLE = new ModRole();
                public static final MemberRole MEMBER_ROLE = new MemberRole();
                protected final List<String> permissions = new ArrayList<>();

                @Nullable
                public abstract String prefix();

                public boolean hasPermission(String permission)
                {
                    return this.permissions.contains(permission);
                }

                public boolean isLeader()
                {
                    return this.getClass().equals(LeaderRole.class);
                }

                public boolean isMod()
                {
                    return this.getClass().equals(ModRole.class);
                }

                public static class LeaderRole extends Role
                {
                    @Override
                    public @Nullable String prefix()
                    {
                        return "LEADER";
                    }

                    @Override
                    public boolean hasPermission(String permission)
                    {
                        return true;
                    }
                }

                public static class ModRole extends Role
                {
                    public ModRole()
                    {
                        this.permissions.add("invite");
                        this.permissions.add("kick");
                    }

                    @Override
                    public @Nullable String prefix()
                    {
                        return "MOD";
                    }
                }

                public static class MemberRole extends Role
                {
                    @Override
                    public @Nullable String prefix()
                    {
                        return null;
                    }

                    @Override
                    public boolean hasPermission(String permission)
                    {
                        return false;
                    }
                }
            }

            public static class RoleAdapter implements JsonSerializer<Role>, JsonDeserializer<Role>
            {
                @Override
                public JsonElement serialize(Role src, Type typeOfSrc, JsonSerializationContext context)
                {
                    return new JsonPrimitive(src.getClass().getName());
                }

                @Override
                public Role deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
                {
                    final JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();

                    Role role = null;

                    try
                    {
                        role = (Role) Class.forName(jsonprimitive.getAsString()).getDeclaredConstructor().newInstance();
                    }
                    catch (Exception e)
                    {
                        role = Role.MEMBER_ROLE;
                    }

                    return role;
                }
            }
        }
    }
}
