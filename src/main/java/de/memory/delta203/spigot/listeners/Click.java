package de.memory.delta203.spigot.listeners;

import de.memory.delta203.spigot.Memory;
import de.memory.delta203.spigot.game.Game;
import de.memory.delta203.spigot.utils.ItemBuilder;
import java.util.Objects;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Click implements Listener {

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    // player is in game
    if (!Memory.gameHandler.playerIsInGame(p)) return;
    Game game = Memory.gameHandler.getGame(p);
    if (e.getClickedInventory() != game.getInventory()) return;
    e.setCancelled(true);
    try {
      if (Objects.requireNonNull(e.getCurrentItem()).getType() != Material.PAINTING) return;
    } catch (Exception ex) {
      return;
    }
    // look for players turn
    if (game.current != p) return;

    // valid
    int slot = e.getSlot();
    game.turns.add(slot);
    game.getInventory().setItem(slot, new ItemStack(game.getMaterials().get(slot)));
    if (game.pairIsFound()) {
      game.getHost()
          .playSound(game.getHost().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
      game.getTarget()
          .playSound(game.getTarget().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    } else {
      game.getHost().playSound(game.getHost().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 2);
      game.getTarget().playSound(game.getTarget().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 2);
    }

    // two turns have made
    if (game.turns.size() != 2) return;
    final Player[] next = {game.getNextPlayer()};
    game.current = null;
    Bukkit.getScheduler()
        .scheduleSyncDelayedTask(
            Memory.plugin,
            () -> {
              if (game.pairIsFound()) {
                // pair found
                for (int i = 0; i < 2; i++) game.getInventory().setItem(game.turns.get(i), null);
                game.scores.put(p, game.scores.get(p) + 1);
                next[0] = p;
              } else {
                for (int i = 0; i < 2; i++)
                  game.getInventory()
                      .setItem(
                          game.turns.get(i),
                          new ItemBuilder(
                                  new ItemStack(Material.PAINTING),
                                  Memory.configYml.get().getString("inventory.card"))
                              .getItem());
              }

              game.turns.clear();
              game.current = next[0];

              ComponentBuilder builder = new ComponentBuilder();
              builder.append(new TextComponent(game.getTitle()));
              Memory.titleHandler.setPlayerInventoryTitle(game.getHost(), builder.getParts());
              Memory.titleHandler.setPlayerInventoryTitle(game.getTarget(), builder.getParts());

              if (game.gameIsFinished()) {
                Player winner = game.getWinner();
                if (winner == null) {
                  game.getHost()
                      .playSound(game.getHost().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                  game.getTarget()
                      .playSound(game.getTarget().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                  game.getHost()
                      .sendMessage(
                          Memory.prefix + Memory.configYml.get().getString("finished.no_winner"));
                  game.getTarget()
                      .sendMessage(
                          Memory.prefix + Memory.configYml.get().getString("finished.no_winner"));
                } else {
                  Memory.gameHandler.sendWinnerMessage(game, winner);
                }
                Memory.gameHandler.deleteGame(game);
                game.getHost().closeInventory();
                game.getTarget().closeInventory();
              }
            },
            30);
  }
}
