package id.rajaopak.common.utils;

import id.rajaopak.common.OpakLibrary;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Task {

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(OpakLibrary.getInstance(), runnable);
    }

    public static void syncLater(long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(OpakLibrary.getInstance(), runnable, delay);
    }

    public static BukkitTask syncTimer(long delay, long runEvery, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimer(OpakLibrary.getInstance(), runnable, delay, runEvery);
    }

    public static BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(OpakLibrary.getInstance(), runnable);
    }

    public static void asyncLater(long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(OpakLibrary.getInstance(), runnable, delay);
    }

    public static void asyncTimer(long delay, long runEvery, Runnable runnable) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(OpakLibrary.getInstance(), runnable, delay, runEvery);
    }

}
