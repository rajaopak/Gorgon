package id.rajaopak.gorgon.config;

import id.rajaopak.common.config.CustomConfig;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CacheFile extends CustomConfig {

    public CacheFile(String configName, String directory) {
        super(configName, directory);
    }

    public boolean hasKey(String key, Player player) {
        return this.getConfig().getStringList(key).contains(player.getUniqueId().toString());
    }

    public void setKey(String key, Player player) {
        List<String> list = this.getConfig().getStringList(key);

        list.add(player.getUniqueId().toString());

        this.getConfig().set(key, list);
        this.saveConfig();
    }

    public void removeKey(String key, Player player) {
        List<String> list = this.getConfig().getStringList(key);

        list.remove(player.getUniqueId().toString());

        this.getConfig().set(key, list);
        this.saveConfig();
    }

}
