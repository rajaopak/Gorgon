package id.rajaopak.gorgon.utils;

import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.config.LanguageFile;
import org.bukkit.command.CommandSender;

public class PermissionChecker {

    public static boolean check(CommandSender sender, String permission) {
        return check(sender, permission, false);
    }

    public static boolean check(CommandSender sender, String permission, boolean others) {
        if (others) {
            if (sender.hasPermission("gorgon." + permission + ".others")) {
                return true;
            } else {
                ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
                return false;
            }
        }

        if (sender.hasPermission("gorgon." + permission)) {
            return true;
        } else {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return false;
        }
    }
}
