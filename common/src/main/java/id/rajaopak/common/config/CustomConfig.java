package id.rajaopak.common.config;

import id.rajaopak.common.OpakLibrary;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class CustomConfig {

    private final File file;
    private FileConfiguration config;

    public CustomConfig(String configName, String directory) {
        Plugin plugin = OpakLibrary.getInstance();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (directory == null) {
            file = new File(plugin.getDataFolder(), configName);

            if (!file.exists()) {
                if (plugin.getResource(configName) != null) {
                    plugin.saveResource(configName, false);
                } else {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            File directoryFile = new File(plugin.getDataFolder() + File.separator + directory);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }

            file = new File(plugin.getDataFolder() + File.separator + directory, configName);

            if (!file.exists()) {
                if (plugin.getResource(configName) != null) {
                    plugin.saveResource(configName, false);
                } else {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
