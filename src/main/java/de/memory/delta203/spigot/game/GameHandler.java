package de.memory.delta203.spigot.game;

import de.memory.delta203.spigot.Memory;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class GameHandler {

  private final HashMap<Player, Player> invites;
  private final HashMap<Player, Game> players;

  public GameHandler() {
    invites = new HashMap<>();
    players = new HashMap<>();
  }

  public HashMap<Player, Player> getInvites() {
    return invites;
  }

  public void createGame(Player host, Player target) {
    Game game = new Game(host, target);
    players.put(host, game);
    players.put(target, game);
    host.openInventory(game.getInventory());
    target.openInventory(game.getInventory());
    host.playSound(host.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    target.playSound(host.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
  }

  public void deleteGame(Game game) {
    players.remove(game.getHost());
    players.remove(game.getTarget());
  }

  public void sendWinnerMessage(Game game, Player winner) {
    game.getHost().playSound(game.getHost().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    game.getTarget()
        .playSound(game.getTarget().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    game.getHost()
        .sendMessage(
            Memory.prefix
                + Objects.requireNonNull(Memory.configYml.get().getString("finished.winner"))
                    .replace("%player%", winner.getName()));
    game.getTarget()
        .sendMessage(
            Memory.prefix
                + Objects.requireNonNull(Memory.configYml.get().getString("finished.winner"))
                    .replace("%player%", winner.getName()));
  }

  public boolean playerIsInGame(Player p) {
    return players.containsKey(p);
  }

  public Game getGame(Player p) {
    return players.get(p);
  }
}
