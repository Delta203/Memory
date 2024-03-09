package de.memory.delta203.spigot.commands;

import de.memory.delta203.spigot.Memory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class Commands implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player p)) return false;
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("invite")) {
        Player target = Bukkit.getPlayer(args[1]);
        // target is online
        if (target == null) {
          p.sendMessage(
              Memory.prefix
                  + Objects.requireNonNull(Memory.configYml.get().getString("not_online"))
                      .replace("%player%", args[1]));
          return false;
        }
        // target is player
        if (target == p) {
          p.sendMessage(Memory.prefix + Memory.configYml.get().getString("not_urself"));
          return false;
        }
        // already invited
        if (Memory.gameHandler.getInvites().containsKey(p)) {
          if (Memory.gameHandler.getInvites().get(p) == target) {
            p.sendMessage(
                Memory.prefix
                    + Objects.requireNonNull(Memory.configYml.get().getString("request.already"))
                        .replace("%player%", target.getName()));
            return false;
          }
        }

        // valid
        Memory.gameHandler.getInvites().put(p, target);
        Memory.gameHandler.getInvites().put(target, p);

        TextComponent message =
            new TextComponent(
                Memory.prefix
                    + Objects.requireNonNull(Memory.configYml.get().getString("request.received"))
                        .replace("%player%", p.getName()));
        TextComponent accept =
            new TextComponent(Memory.configYml.get().getString("request.accept"));
        accept.setClickEvent(
            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/memory accept " + p.getName()));
        TextComponent deny = new TextComponent(Memory.configYml.get().getString("request.deny"));
        deny.setClickEvent(
            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/memory deny " + p.getName()));
        message.addExtra(accept);
        message.addExtra(deny);
        target.spigot().sendMessage(message);
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        p.sendMessage(
            Memory.prefix
                + Objects.requireNonNull(Memory.configYml.get().getString("request.sent"))
                    .replace("%player%", target.getName()));
      } else if (args[0].equalsIgnoreCase("accept")) {
        Player inviter = getInviter(p);
        if (inviter == null) return false;
        Memory.gameHandler.createGame(inviter, p);
      } else if (args[0].equalsIgnoreCase("deny")) {
        Player inviter = getInviter(p);
        if (inviter == null) return false;
        inviter.sendMessage(
            Memory.prefix
                + Objects.requireNonNull(Memory.configYml.get().getString("requesting.denied"))
                    .replace("%player%", p.getName()));
        p.sendMessage(Memory.prefix + Memory.configYml.get().getString("requesting.you_denied"));
      } else {
        sendHelp(p);
      }
    } else {
      sendHelp(p);
    }
    return false;
  }

  private void sendHelp(Player p) {
    p.sendMessage(Memory.prefix + Memory.configYml.get().getString("help.invite"));
    p.sendMessage(Memory.prefix + Memory.configYml.get().getString("help.accept"));
    p.sendMessage(Memory.prefix + Memory.configYml.get().getString("help.deny"));
  }

  private Player getInviter(Player p) {
    // was invited
    if (!Memory.gameHandler.getInvites().containsKey(p)) {
      p.sendMessage(Memory.prefix + Memory.configYml.get().getString("requesting.no_invite"));
      return null;
    }
    Player inviter = Memory.gameHandler.getInvites().get(p);
    // inviter is online
    if (!inviter.isOnline()) {
      p.sendMessage(
          Memory.prefix
              + Objects.requireNonNull(Memory.configYml.get().getString("requesting.not_online"))
                  .replace("%player%", inviter.getName()));
      Memory.gameHandler.getInvites().remove(p);
      return null;
    }
    // inviter has no invite
    if (!Memory.gameHandler.getInvites().containsKey(inviter)) {
      p.sendMessage(
          Memory.prefix
              + Objects.requireNonNull(
                      Memory.configYml.get().getString("requesting.no_invite_received"))
                  .replace("%player%", inviter.getName()));
      Memory.gameHandler.getInvites().remove(p);
      return null;
    }
    if (Memory.gameHandler.getInvites().get(inviter) != p) {
      p.sendMessage(
          Memory.prefix
              + Objects.requireNonNull(
                      Memory.configYml.get().getString("requesting.no_invite_received"))
                  .replace("%player%", inviter.getName()));
      Memory.gameHandler.getInvites().remove(p);
      return null;
    }
    Memory.gameHandler.getInvites().remove(p);
    Memory.gameHandler.getInvites().remove(inviter);
    return inviter;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length <= 1) {
      List<String> arguments = new ArrayList<>();
      arguments.add("invite");
      arguments.add("accept");
      arguments.add("deny");
      return arguments;
    }
    return null;
  }
}
