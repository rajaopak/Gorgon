package id.rajaopak.gorgon.listener;

import de.tr7zw.nbtapi.NBTItem;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickUpItemListener implements Listener {

    private final Gorgon plugin;

    public PickUpItemListener(Gorgon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPickUpBanItem(EntityPickupItemEvent e) {
        NBTItem item = new NBTItem(e.getItem().getItemStack());

        if (item.getBoolean("IsBanItem")) {
            if (!(e.getEntity() instanceof Player p)) return;
            if (PermissionChecker.check(e.getEntity(), "bypass.banitem")) return;

            e.setCancelled(true);
            if (this.plugin.getServer().getPluginManager().isPluginEnabled("LiteBans")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + p.getName() + " " + LanguageFile.getTrollBanItemPickUp());
            } else {
                p.kickPlayer(ChatUtil.color(LanguageFile.getTrollBanItemPickUp()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTogglePickUpItem(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (this.plugin.getCacheFile().hasKey("togglepickup", p)) {
                e.setCancelled(true);
            }
        }
    }
}
