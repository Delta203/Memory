package de.memory.delta203.spigot.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.memory.delta203.spigot.Memory;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TitleHandler {

  private final HashMap<UUID, InventoryPlayer> inventoryPlayers = new HashMap<>();
  private final ProtocolManager protocolManager;

  public TitleHandler(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  public void registerPacketListeners() {
    protocolManager.addPacketListener(getOpenWindowPacketListener());
    protocolManager.addPacketListener(getCloseWindowPacketListener());
  }

  private PacketListener getOpenWindowPacketListener() {
    return new PacketAdapter(
        Memory.plugin, ListenerPriority.HIGH, PacketType.Play.Server.OPEN_WINDOW) {
      @Override
      public void onPacketSending(PacketEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final int windowId = event.getPacket().getIntegers().read(0);
        final Object containerType = event.getPacket().getStructures().readSafely(0);
        InventoryPlayer player = new InventoryPlayer(windowId, containerType);
        inventoryPlayers.put(uuid, player);
      }
    };
  }

  private PacketListener getCloseWindowPacketListener() {
    return new PacketAdapter(
        Memory.plugin, ListenerPriority.HIGH, PacketType.Play.Client.CLOSE_WINDOW) {
      @Override
      public void onPacketReceiving(PacketEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        inventoryPlayers.remove(uuid);
      }
    };
  }

  public void setPlayerInventoryTitle(Player player, List<BaseComponent> title) {
    final InventoryType type = player.getOpenInventory().getType();
    if (type == InventoryType.CRAFTING || type == InventoryType.CREATIVE) return;

    InventoryPlayer inventoryPlayer = inventoryPlayers.getOrDefault(player.getUniqueId(), null);

    if (inventoryPlayer == null) return;

    final int windowId = inventoryPlayer.getWindowId();
    if (windowId == 0) return;

    final Object windowType = inventoryPlayer.getContainerType();
    final String titleJson = ComponentSerializer.toString(title);

    // Send the packet
    sendOpenScreenPacket(player, windowId, windowType, titleJson);
    // Update the inventory for the client (to show items)
    player.updateInventory();
  }

  private void sendOpenScreenPacket(
      Player player, int windowId, Object windowType, String titleJson) {
    final WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(titleJson);

    PacketContainer openScreen = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
    openScreen.getIntegers().write(0, windowId);
    openScreen.getStructures().write(0, (InternalStructure) windowType);
    openScreen.getChatComponents().write(0, wrappedChatComponent);

    try {
      protocolManager.sendServerPacket(player, openScreen);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  class InventoryPlayer {
    private final int windowId;
    private final Object containerType;

    public InventoryPlayer(int windowId, Object containerType) {
      this.windowId = windowId;
      this.containerType = containerType;
    }

    public int getWindowId() {
      return windowId;
    }

    public Object getContainerType() {
      return containerType;
    }
  }
}
