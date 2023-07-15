package id.rajaopak.common;

import id.rajaopak.common.gui.GuiBuilderManager;
import id.rajaopak.common.utils.ChatSession;
import id.rajaopak.common.utils.MarkCooldown;
import id.rajaopak.common.utils.PlayerCooldown;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class OpakLibrary {

  private static JavaPlugin instance;
  public static boolean PLACEHOLDER_API = false;

  private static PlayerCooldown cooldown;
  private static MarkCooldown cdMark;

  public static void register(JavaPlugin plugin) {
    instance = plugin;
    cooldown = new PlayerCooldown(plugin);
    cdMark = new MarkCooldown(plugin);
    PLACEHOLDER_API = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

    GuiBuilderManager.register(plugin);

    plugin.getServer().getPluginManager().registerEvents(new ChatSession(), plugin);
  }

  public static Plugin getInstance() {
    return instance;
  }

  public static PlayerCooldown getCooldown() {
    return cooldown;
  }

  public static MarkCooldown getCooldownMark() {
    return cdMark;
  }
}
