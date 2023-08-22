package id.rajaopak.gorgon.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.commands.BaseCommand;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SantetCommand extends BaseCommand {

    private final Gorgon plugin;

    public SantetCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("santet <player>")
    @CommandPermission("gorgon.troll.santet")
    public void santet(final @NonNull CommandSender sender, final @NonNull @Argument(value = "player", suggestions = "players") String targetName) {
        if (!PermissionChecker.check(sender, "troll.santet")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return;
        }

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            ChatUtil.sendMessage(sender, LanguageFile.getPlayerNotFound());
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 100, false, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100, false, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 100, false, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 100, false, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, false, false, false));
        target.getWorld().strikeLightningEffect(target.getLocation());
        target.sendTitle(ChatUtil.color("&c&lSANTET"), "", 0, 40, 0);
    }
}
