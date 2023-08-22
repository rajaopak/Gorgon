package id.rajaopak.gorgon.structure;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CanSkipCooldown(CommandSender sender, String action, boolean skip, long seconds, @Nullable String permission, @Nullable List<String> warnings) {
}
