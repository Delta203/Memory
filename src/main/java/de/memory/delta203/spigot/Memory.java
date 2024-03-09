package de.memory.delta203.spigot;

import com.comphenix.protocol.ProtocolLibrary;
import de.memory.delta203.spigot.commands.Commands;
import de.memory.delta203.spigot.files.FileManager;
import de.memory.delta203.spigot.game.GameHandler;
import de.memory.delta203.spigot.listeners.Click;
import de.memory.delta203.spigot.listeners.Close;
import de.memory.delta203.spigot.listeners.Quit;
import de.memory.delta203.spigot.utils.TitleHandler;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Memory extends JavaPlugin {

  public static Memory plugin;
  public static String prefix;
  public static FileManager configYml;
  public static GameHandler gameHandler;
  public static TitleHandler titleHandler;

  @Override
  public void onEnable() {
    plugin = this;

    configYml = new FileManager("config.yml");
    configYml.create();
    configYml.load();

    prefix = configYml.get().getString("prefix");
    gameHandler = new GameHandler();
    titleHandler = new TitleHandler(ProtocolLibrary.getProtocolManager());
    titleHandler.registerPacketListeners();

    Objects.requireNonNull(getCommand("memory")).setExecutor(new Commands());
    Bukkit.getPluginManager().registerEvents(new Click(), this);
    Bukkit.getPluginManager().registerEvents(new Close(), this);
    Bukkit.getPluginManager().registerEvents(new Quit(), this);

    Bukkit.getConsoleSender().sendMessage(prefix + configYml.get().getString("loaded"));
  }
}
