package com.github.neapovil.parties.command;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.neapovil.parties.resource.PartiesResource;
import com.github.neapovil.parties.runnable.SaveRunnable;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class PromoteCommand extends AbstractCommand
{
    public void register()
    {
        new CommandAPICommand("party")
                .withPermission("parties.command.promote")
                .withArguments(new MultiLiteralArgument("command", "promote", "demote").withRequirement(sender -> {
                    final Player player = (Player) sender;
                    final Optional<PartiesResource.Party.Member> optionalmember = plugin.findMember(player);
                    return optionalmember.isPresent() && optionalmember.get().role.isLeader();
                }))
                .withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    final Player player = (Player) info.sender();
                    return plugin.findParty(player).get().members
                            .stream()
                            .map(i -> i.username)
                            .filter(i -> !i.startsWith(player.getName()))
                            .toArray(String[]::new);
                })))
                .withArguments(new StringArgument("rank").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    if (info.previousArgs().get("command").equals("promote"))
                    {
                        return new String[] { "leader", "mod" };
                    }

                    return new String[] { "member" };
                })))
                .executesPlayer((player, args) -> {
                    final String command = (String) args.get("command");
                    final String player1name = (String) args.get("player");
                    final String rank = (String) args.get("rank");

                    if (player.getName().equalsIgnoreCase(player1name))
                    {
                        return;
                    }

                    plugin.findParty(player).ifPresent(party -> {
                        party.findMember(player1name).ifPresent(member -> {
                            String playermessage = "";
                            String player1message = "";

                            if (command.equals("promote"))
                            {
                                if (rank.equals("leader"))
                                {
                                    member.role = PartiesResource.Party.Member.Role.LEADER_ROLE;
                                    player1message = "You have been promoted party leader";
                                    playermessage = "You are no longer party leader";

                                    party.findMember(player).ifPresent(member1 -> {
                                        member1.role = PartiesResource.Party.Member.Role.MEMBER_ROLE;
                                    });

                                    CommandAPI.updateRequirements(player);
                                }

                                if (rank.equals("mod"))
                                {
                                    member.role = PartiesResource.Party.Member.Role.MOD_ROLE;
                                    player1message = "You have been promoted party moderator";
                                    playermessage = "You promoted %s party moderator".formatted(player1name);
                                }
                            }

                            if (command.equals("demote"))
                            {
                                member.role = PartiesResource.Party.Member.Role.MEMBER_ROLE;
                                player1message = "You have been demoted to party member";
                                playermessage = "You demoted %s to party member".formatted(player1name);
                            }

                            party.onlineMembers().forEach(i -> {
                                if (!i.getUniqueId().equals(member.uuid))
                                {
                                    i.sendMessage("%s's party rank changed to: %s".formatted(player1name, rank));
                                }
                            });

                            player.sendMessage(playermessage);

                            final Player player1 = plugin.getServer().getPlayer(player1name);

                            if (player1 != null)
                            {
                                player1.sendMessage(player1message);
                                CommandAPI.updateRequirements(player1);
                            }

                            new SaveRunnable().runTaskAsynchronously(plugin);
                        });
                    });
                })
                .register();
    }
}
