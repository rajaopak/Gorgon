package id.rajaopak.gorgon.utils;

import org.bukkit.command.CommandSender;

public class PermissionChecker {

    public static boolean check(CommandSender sender, String permission) {
        return sender.hasPermission("gorgon." + permission);
    }
}
