package id.rajaopak.gorgon.module.helpme;

import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.enums.HelpMeState;
import id.rajaopak.gorgon.module.helpme.HelpMeData;
import id.rajaopak.gorgon.utils.VanishChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class HelpMeManager {

    private final Gorgon plugiin;
    private final HashMap<UUID, HelpMeData> helpme;

    public HelpMeManager(Gorgon plugin) {
        this.plugiin = plugin;
        this.helpme = new HashMap<>();
    }

    public void addHelpMe(UUID uuid, HelpMeData data) {
        if (containsHelpMe(uuid)) return;
        this.helpme.put(uuid, data);
    }

    public boolean containsHelpMe(UUID uuid) {
        return this.helpme.containsKey(uuid);
    }

    public boolean containsPlayer(UUID playeruuid) {
        return this.helpme.values().stream().anyMatch(helpMeData -> helpMeData.getSenderUUID().equals(playeruuid));
    }

    public HelpMeData getHelpMe(UUID uuid) {
        return this.helpme.get(uuid);
    }

    public HelpMeData getByPlayer(UUID uuid) {
        return this.helpme.values().stream().filter(helpMeData -> helpMeData.getSenderUUID().equals(uuid)).findFirst().orElse(null);
    }

    public void removeHelpMe(UUID uuid) {
        this.helpme.remove(uuid);
    }

    public void clear() {
        this.helpme.clear();
    }

    public void notifyPlayerHelpMe(Player player, @NotNull HelpMeData data) {
        if (data.getState() == HelpMeState.WAITING) {
            return;
        }

        Player target = Bukkit.getPlayer(data.getSenderUUID());

        if (target == null || !target.isOnline()) {
            ChatUtil.sendMessage(player, LanguageFile.getHelpMeTargetOffline(data.getSenderName()));
            return;
        }

        if (data.getState() == HelpMeState.ACCEPTED) {
            if (VanishChecker.isVanished(player)) {
                ChatUtil.sendMessage(target, LanguageFile.getHelpMeAcceptVanish());
            } else {
                ChatUtil.sendMessage(target, LanguageFile.getHelpMeAccept(player));
            }
        } else if (data.getState() == HelpMeState.DECLINED) {
            if (VanishChecker.isVanished(player)) {
                ChatUtil.sendMessage(target, LanguageFile.getHelpMeDeclineVanish());
            } else {
                ChatUtil.sendMessage(target, LanguageFile.getHelpMeDecline(player));
            }
        } else if (data.getState() == HelpMeState.FINISH) {
            if (VanishChecker.isVanished(player)) {
                ChatUtil.sendMessage(target, LanguageFile.getHelpMeFinishVanish());
            } else {
                ChatUtil.sendMessage(target, LanguageFile.getHelpMeFinish(player));
            }
        }

    }
}
