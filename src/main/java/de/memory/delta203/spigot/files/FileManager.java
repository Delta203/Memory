package de.memory.delta203.spigot.files;

import de.memory.delta203.spigot.Memory;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class FileManager {

  private final String filename;
  private final File file;
  private FileConfiguration cfg;

  /**
   * Register a FileManager with the specified filename to handle a configuration file.
   *
   * @param filename the name of the config file
   */
  public FileManager(String filename) {
    this.filename = filename;
    file = new File(Memory.plugin.getDataFolder(), filename);
  }

  /**
   * Create the config file if it does not exist. If the plugins data folder does not exist, create
   * it as well.
   */
  public void create() {
    if (!Memory.plugin.getDataFolder().exists()) {
      Memory.plugin.getDataFolder().mkdir();
    }
    try {
      if (!file.exists()) {
        Files.copy(Objects.requireNonNull(Memory.plugin.getResource(filename)), file.toPath());
      }
      cfg = YamlConfiguration.loadConfiguration(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Loads the configuration from the file. */
  public void load() {
    try {
      cfg.load(file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  /** Saves the configuration to the file. */
  public void save() {
    try {
      cfg.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the configuration object.
   *
   * @return the file configuration
   */
  public Configuration get() {
    return cfg;
  }
}
