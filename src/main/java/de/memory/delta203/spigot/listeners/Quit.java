package de.memory.delta203.spigot.listeners;

import de.memory.delta203.spigot.Memory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    act(p);
  }

  @EventHandler
  public void onKick(PlayerKickEvent e) {
    Player p = e.getPlayer();
    act(p);
  }

  private void act(Player p) {
    Memory.gameHandler.getInvites().remove(p);
  }
}
