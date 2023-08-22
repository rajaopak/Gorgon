package id.rajaopak.gorgon.module.helpme;

import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.module.helpme.gui.HelpMeGui;
import id.rajaopak.gorgon.module.helpme.HelpMeData;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class StaffHelpMeManager {

    private final Gorgon plugin;
    private final HashMap<UUID, HelpMeData> staffInHelpMe;

    public StaffHelpMeManager(Gorgon plugin) {
        this.plugin = plugin;
        this.staffInHelpMe = new HashMap<>();
    }

    public void addStaffInHelpMe(UUID staffuuid, HelpMeData data) {
        if (containsStaff(staffuuid)) return;
        this.staffInHelpMe.put(staffuuid, data);
    }

    public boolean containsStaff(UUID staffuuid) {
        return this.staffInHelpMe.containsKey(staffuuid);
    }

    public HelpMeData getHelpMe(UUID staffuuid) {
        return this.staffInHelpMe.get(staffuuid);
    }

    public void removeStaff(UUID staffuuid) {
        this.staffInHelpMe.remove(staffuuid);
    }

    public void sendHelpMeToStaff(HelpMeData data) {
        Bukkit.getOnlinePlayers().stream().filter(this::checkIfStaff).forEach(player -> {
            HelpMeGui gui = new HelpMeGui(player);
            gui.acceptHelpMePage(data);
        });
    }

    public boolean checkIfStaff(@NotNull Player player) {
        return PermissionChecker.check(player, "helpme.staff");
    }

    public void clear() {
        this.staffInHelpMe.clear();
    }

}
