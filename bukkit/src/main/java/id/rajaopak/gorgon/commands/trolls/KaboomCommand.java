package id.rajaopak.gorgon.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.specifier.Range;
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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public class KaboomCommand extends BaseCommand {

    private final Gorgon plugin;

    public KaboomCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("kaboom|boom [player]")
    @CommandPermission("gorgon.troll.kaboom")
    public void kaboom(final @NonNull CommandSender sender, final @Nullable @Argument(value = "player", suggestions = "players") String targetName,
                       final @Nullable @Flag("blind") Boolean isBlind,
                       final @Nullable @Flag("power") @Range(min = "1", max = "5") Integer power) {
        if (!PermissionChecker.check(sender, "troll.kaboom")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return;
        }

        if (targetName == null) {
            if (!(sender instanceof Player player)) {
                this.plugin.getMinecraftHelp().queryCommands("kaboom", sender);
                return;
            }

            player.setVelocity(player.getLocation().getDirection().setY(Objects.requireNonNullElse(power, 5)));
            player.getWorld().strikeLightningEffect(player.getLocation());
        } else {
            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                ChatUtil.sendMessage(sender, LanguageFile.getPlayerNotFound());
                return;
            }

            if (Boolean.TRUE.equals(isBlind)) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 100, false, false, false));
            }

            target.setVelocity(target.getLocation().getDirection().setY(Objects.requireNonNullElse(power, 5)));
            target.getWorld().strikeLightningEffect(target.getLocation());
            target.sendTitle(ChatUtil.color("&a&lKABOOM"), "", 0, 40, 0);
        }
    }
}
