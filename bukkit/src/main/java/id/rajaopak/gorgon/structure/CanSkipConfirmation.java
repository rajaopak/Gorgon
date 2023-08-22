package id.rajaopak.gorgon.structure;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CanSkipConfirmation(CommandSender sender, boolean canSkip, @Nullable List<String> reason) {
}