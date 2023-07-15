package id.rajaopak.gorgon.config;

import org.bukkit.entity.Player;

import static id.rajaopak.gorgon.Gorgon.tl;

public class LanguageFile {

    public static String getPrefix() {
        return tl("prefix");
    }

    public static String getNoPermission() {
        return tl("nopermission");
    }

    public static String getCommandOnlyPlayer() {
        return tl("command.onlyplayer");
    }

    public static String getHelpMeAccept(Player player) {
        return tl("helpme.accept", player.getName());
    }

    public static String getHelpMeAcceptVanish() {
        return tl("helpme.accept.vanish");
    }

    public static String getHelpMeDecline(Player player) {
        return tl("helpme.declined", player.getName());
    }

    public static String getHelpMeDeclineVanish() {
        return tl("helpme.declined.vanish");
    }

    public static String getHelpMeFinish(Player player) {
        return tl("helpme.finish", player.getName());
    }

    public static String getHelpMeFinishVanish() {
        return tl("helpme.finish.vanish");
    }

    public static String getHelpMeTargetOffline(String name) {
        return tl("helpme.target.offline", name);
    }

}
