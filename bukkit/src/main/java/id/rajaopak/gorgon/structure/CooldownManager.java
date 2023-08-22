package id.rajaopak.gorgon.structure;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private final Cache<String, Long> cache;

    public CooldownManager() {
        this.cache = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.SECONDS).expireAfterAccess(5, TimeUnit.MINUTES).build();
    }

    public void requestCooldown(Callback callback, CommandSender sender, String action, boolean skip, long seconds, @Nullable String permissiom, @Nullable List<String> warnings) {
        if (skip || sender instanceof ConsoleCommandSender) {
            callback.call();
            return;
        }

        Player p = (Player) sender;

        if (permissiom != null && !permissiom.isEmpty()) {
            if (sender.hasPermission(permissiom + ".bypass")) {
                callback.call();
                return;
            }
        }

        if (!hasCooldown(p.getUniqueId(), action)) {
            callback.call();
            cache.put(p.getUniqueId() + "-" + action, System.currentTimeMillis() + (seconds * 1000));
            return;
        }

        if (getCooldown(p.getUniqueId(), action) < System.currentTimeMillis()) {
            callback.call();
            cache.invalidate(p.getUniqueId() + "-" + action);
            cache.put(p.getUniqueId() + "-" + action, System.currentTimeMillis() + (seconds * 1000));
            return;
        }

        if (warnings != null)
            ChatUtil.sendMessage(sender, warnings);

        ChatUtil.sendMessage(sender, "&cYou can't use this command for another &e" + Utils.formatTime((int) ((System.currentTimeMillis() - getCooldown(p.getUniqueId(), action)) / 1000)) + " &cseconds.");
    }

    public void requestCooldown(Callback callback, CanSkipCooldown cooldown)  {
        this.requestCooldown(callback, cooldown.sender(), cooldown.action(), cooldown.skip(), cooldown.seconds(), cooldown.permission(), cooldown.warnings());
    }

    @Nullable
    public Long getCooldown(UUID uuid, String action) {
        return cache.getIfPresent(uuid + "-" + action);
    }

    public boolean hasCooldown(UUID uuid, String action) {
        return getCooldown(uuid, action) != null;
    }

    public void clear() {
        cache.invalidateAll();
    }

}
