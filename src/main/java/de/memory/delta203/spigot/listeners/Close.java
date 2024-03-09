package de.memory.delta203.spigot.listeners;

import de.memory.delta203.spigot.Memory;
import de.memory.delta203.spigot.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Close implements Listener {

  @EventHandler
  public void onInventory(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();
    // player is in game
    if (!Memory.gameHandler.playerIsInGame(p)) return;
    Game game = Memory.gameHandler.getGame(p);
    if (e.getInventory() != game.getInventory()) return;
    Player winner = null;
    if (game.getHost() == p) winner = game.getTarget();
    if (game.getTarget() == p) winner = game.getHost();
    assert winner != null;
    Memory.gameHandler.sendWinnerMessage(game, winner);
    Memory.gameHandler.deleteGame(game);
    game.getHost().closeInventory();
    game.getTarget().closeInventory();
  }
}
