package id.rajaopak.common.utils;

import org.bukkit.Bukkit;

public class VersionChecker {

    private static final String nmsVersion;

    static {
        nmsVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }

}
