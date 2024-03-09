package de.memory.delta203.spigot.utils;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ItemBuilder {

  private final ItemStack item;
  private final String name;
  private final String lore;
  private final Color color;
  private final String skullOwner;

  /**
   * Register an ItemBuilder with the specified item and name to modify ItemStacks.
   *
   * @param item the {@link ItemStack} to be built
   * @param name the display name of the ItemStack
   */
  public ItemBuilder(ItemStack item, String name) {
    this.item = item;
    this.name = name;
    this.lore = null;
    this.color = null;
    this.skullOwner = null;
  }

  /**
   * Register an ItemBuilder with the specified item, name and lore to modify ItemStacks.
   *
   * @param item the {@link ItemStack} to be built
   * @param name the display name of the ItemStack
   * @param lore the lore of the ItemStack
   */
  public ItemBuilder(ItemStack item, String name, String lore) {
    this.item = item;
    this.name = name;
    this.lore = lore;
    this.color = null;
    this.skullOwner = null;
  }

  /**
   * Register an ItemBuilder with the specified item, name, lore and color to modify ItemStacks.
   *
   * @param item the {@link ItemStack} to be built
   * @param name the display name of the ItemStack
   * @param lore the lore of the ItemStack
   * @param color the color of the ItemStack (for {@link LeatherArmorMeta})
   */
  public ItemBuilder(ItemStack item, String name, String lore, Color color) {
    this.item = item;
    this.name = name;
    this.lore = lore;
    this.color = color;
    this.skullOwner = null;
  }

  /**
   * Register an ItemBuilder with the specified item, name, lore and skull owner to modify
   * ItemStacks.
   *
   * @param item the {@link ItemStack} to be built
   * @param name the display name of the ItemStack
   * @param lore the lore of the ItemStack
   * @param skullOwner the skull owner of the ItemStack (for {@link SkullMeta})
   */
  public ItemBuilder(ItemStack item, String name, String lore, String skullOwner) {
    this.item = item;
    this.name = name;
    this.lore = lore;
    this.color = null;
    this.skullOwner = skullOwner;
  }

  /**
   * Gets the constructed ItemStack with the specified name and lore.
   *
   * @return the constructed ItemStack
   */
  public ItemStack getItem() {
    ItemMeta meta = item.getItemMeta();
    Objects.requireNonNull(meta).setDisplayName(name);
    if (lore != null) {
      String[] la = lore.split("%s%");
      ArrayList<String> a = new ArrayList<>(Arrays.asList(la));
      meta.setLore(a);
    }
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Gets the constructed ItemStack with the specified name, lore and color.
   *
   * @return the constructed ItemStack
   */
  public ItemStack getLeatherItem() {
    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    Objects.requireNonNull(meta).setDisplayName(name);
    if (lore != null) {
      String[] la = lore.split("%s%");
      ArrayList<String> a = new ArrayList<>(Arrays.asList(la));
      meta.setLore(a);
    }
    if (color != null) meta.setColor(color);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Gets the constructed ItemStack with the specified name, lore and skull owner.
   *
   * @return the constructed ItemStack
   */
  @SuppressWarnings("deprecation")
  public ItemStack getSkullItem() {
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    Objects.requireNonNull(meta).setDisplayName(name);
    if (lore != null) {
      String[] la = lore.split("%s%");
      ArrayList<String> a = new ArrayList<>(Arrays.asList(la));
      meta.setLore(a);
    }
    if (skullOwner != null) meta.setOwner(skullOwner);
    item.setItemMeta(meta);
    return item;
  }
}
