package id.rajaopak.gorgon.structure;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import id.rajaopak.common.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConfirmationManager {

    private final Cache<UUID, Callback> cache;

    public ConfirmationManager() {
        this.cache = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.SECONDS).expireAfterAccess(5, TimeUnit.MINUTES).build();
    }
    
    /**
     * Requests a confirmation before executing something
     *
     * @param callback the code to execute if confirmed
     * @param sender   the executor
     * @param skip     the required condition to skip the confirmation
     * @param warnings warnings to be sent to the player
     */
    public void requestConfirm(Callback callback, CommandSender sender, boolean skip, @Nullable List<String> warnings) {
        if (skip || sender instanceof ConsoleCommandSender) {
            callback.call();
            return;
        }

        Player player = (Player) sender;
        if (warnings != null && !warnings.isEmpty()) {
            ChatUtil.sendMessage(player, warnings, true);
        }

        ChatUtil.sendMessage(player, "&7Type &e/gorgon confirm &7to confirm your action.", true);
        this.cache.put(player.getUniqueId(), callback);
    }

    /**
     * @see ConfirmationManager#requestConfirm(Callback, CommandSender, boolean, List)
     */
    public void requestConfirm(Callback Callback, CommandSender sender, boolean skip) {
        this.requestConfirm(Callback, sender, skip, null);
    }

    /**
     * @see ConfirmationManager#requestConfirm(Callback, CommandSender, boolean, List)
     */
    public void requestConfirm(Callback Callback, CommandSender sender, @Nullable List<String> warnings) {
        this.requestConfirm(Callback, sender, false, warnings);
    }

    /**
     * @see ConfirmationManager#requestConfirm(Callback, CommandSender, boolean, List)
     */
    public void requestConfirm(Callback Callback, CommandSender sender) {
        this.requestConfirm(Callback, sender, false, null);
    }

    /**
     * @see ConfirmationManager#requestConfirm(Callback, CommandSender, boolean, List)
     */
    public void requestConfirm(Callback Callback, CanSkipConfirmation skipCallback) {
        this.requestConfirm(Callback, skipCallback.sender(), skipCallback.canSkip(), skipCallback.reason());
    }

    /**
     * Confirms the execution of pending code.
     *
     * @param p the player
     */
    public void confirm(Player p) {
        if (!this.cache.asMap().containsKey(p.getUniqueId())) {
            ChatUtil.sendMessage(p, "&cYou don't have any pending action!", true);
        } else {
            ChatUtil.sendMessage(p, "&aAction confirmed.", true);
            this.cache.getIfPresent(p.getUniqueId()).call();
        }
    }

    public void decline(Player p) {
        if (!this.cache.asMap().containsKey(p.getUniqueId())) {
            ChatUtil.sendMessage(p, "&cYou don't have any pending action!", true);
            return;
        }

        ChatUtil.sendMessage(p, "&cAction declined.", true);
        this.cache.invalidate(p.getUniqueId());
    }

    public void clear() {
        cache.invalidateAll();
    }

}
