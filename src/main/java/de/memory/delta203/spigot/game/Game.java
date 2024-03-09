package de.memory.delta203.spigot.game;

import de.memory.delta203.spigot.Memory;
import de.memory.delta203.spigot.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Game {

  public Player current;
  public ArrayList<Integer> turns;
  public HashMap<Player, Integer> scores;

  private final Player host;
  private final Player target;
  private Inventory inventory;
  private final ArrayList<Material> materials;

  public Game(Player host, Player target) {
    this.host = host;
    this.target = target;
    materials = new ArrayList<>();
    this.current = this.host;
    turns = new ArrayList<>();
    scores = new HashMap<>();
    scores.put(host, 0);
    scores.put(target, 0);
    registerInventory();
    registerPairs();
  }

  private void registerInventory() {
    inventory = Bukkit.createInventory(null, 54, getTitle());
    for (int i = 0; i < 54; i++) {
      inventory.setItem(
          i,
          new ItemBuilder(
                  new ItemStack(Material.PAINTING),
                  Memory.configYml.get().getString("inventory.card"))
              .getItem());
    }
  }

  private void registerPairs() {
    int count = 0;
    // pick 27 random materials which has to be an item
    while (count < 27) {
      Material material = Material.values()[new Random().nextInt(Material.values().length)];
      if (CraftMagicNumbers.getItem(material) == null) continue;
      if (material == Material.PAINTING || material == Material.AIR) continue;
      if (materials.contains(material)) continue;
      // add them twice into the list
      materials.add(material);
      materials.add(material);
      count++;
    }
    // shuffle list
    Collections.shuffle(materials);
  }

  public String getTitle() {
    return "§9"
        + (current == host ? "§l§n" : "")
        + host.getName().substring(0, 4)
        + "§r §8"
        + scores.get(host)
        + ":"
        + scores.get(target)
        + " §4"
        + (current == target ? "§l§n" : "")
        + target.getName().substring(0, 4);
  }

  public Player getHost() {
    return host;
  }

  public Player getTarget() {
    return target;
  }

  public Player getNextPlayer() {
    return current == host ? target : host;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public ArrayList<Material> getMaterials() {
    return materials;
  }

  public boolean pairIsFound() {
    if (turns.size() != 2) return false;
    return materials.get(turns.get(0)) == materials.get(turns.get(1));
  }

  public boolean gameIsFinished() {
    return scores.get(host) + scores.get(target) == 27;
  }

  public Player getWinner() {
    if (scores.get(host) > scores.get(target)) return host;
    if (scores.get(host) < scores.get(target)) return target;
    else return null;
  }
}
