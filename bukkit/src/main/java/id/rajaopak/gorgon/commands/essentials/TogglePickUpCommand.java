package id.rajaopak.gorgon.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.commands.BaseCommand;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TogglePickUpCommand extends BaseCommand {

    private final Gorgon plugin;

    public TogglePickUpCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("togglepickup [player] [state]")
    @CommandPermission("gorgon.essentials.togglepickup")
    public void toggleCommand(final @NonNull CommandSender sender,
                              final @Nullable @Argument(value = "state", suggestions = "states") String status,
                              final @Nullable @Argument(value = "player", suggestions = "players", defaultValue = "self") String targetName) {
        if (!PermissionChecker.check(sender, "essentials.togglepickup")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        TargetsCallback targets = this.getTargets(sender, targetName);

        if (targets.isOthers(sender) && !PermissionChecker.check(sender, "essentials.togglepickup", true)) return;

        this.plugin.getCooldownManager().requestCooldown(() -> {
            this.plugin.getConfirmationManager().requestConfirm(() -> {
                AtomicBoolean isToggle = new AtomicBoolean(false);

                targets.stream().forEach(player -> {
                    if (status == null) {
                        if (this.plugin.getCacheFile().hasKey("togglepickup", player)) {
                            this.plugin.getCacheFile().removeKey("togglepickup", player);
                            ChatUtil.sendMessage(player, "&7Toggle pickup &coff&7!", true);
                            isToggle.set(false);
                        } else {
                            this.plugin.getCacheFile().setKey("togglepickup", player);
                            ChatUtil.sendMessage(player, "&7Toggle pickup &aon&7!", true);
                            isToggle.set(true);
                        }
                    } else {
                        if (status.equalsIgnoreCase("on")) {
                            this.plugin.getCacheFile().setKey("togglepickup", player);
                            ChatUtil.sendMessage(player, "&7Toggle pickup &aon&7!", true);
                            isToggle.set(true);
                        } else if (status.equalsIgnoreCase("off")) {
                            this.plugin.getCacheFile().removeKey("togglepickup", player);
                            ChatUtil.sendMessage(player, "&7Toggle pickup &coff&7!", true);
                            isToggle.set(false);
                        } else {
                            this.plugin.getMinecraftHelp().queryCommands("togglepickup", sender);
                        }
                    }
                });

                if (targets.isOthers(sender)) {
                    if (targets.size() == 1) {
                        targets.stream().findFirst().ifPresent(target -> {
                            if (isToggle.get()) {
                                ChatUtil.sendMessage(sender, "&7Toggle pickup &aon &7for &e" + target.getName() + "&7!", true);
                            } else {
                                ChatUtil.sendMessage(sender, "&7Toggle pickup &coff &7for &e" + target.getName() + "&7!", true);
                            }
                        });
                    } else {
                        if (isToggle.get()) {
                            ChatUtil.sendMessage(sender, "&7Toggle pickup &aon &7for &e" + targets.size() + "&7 targets!", true);
                        } else {
                            ChatUtil.sendMessage(sender, "&7Toggle pickup &coff &7for &e" + targets.size() + "&7 targets!", true);
                        }
                    }
                } else if (!(sender instanceof Player) || targets.doesNotContain((Player) sender)) {
                    targets.stream().findFirst().ifPresent(target -> {
                        if (isToggle.get()) {
                            ChatUtil.sendMessage(sender, "&7Toggle pickup &aon &7for &e" + target.getName() + "&7!", true);
                        } else {
                            ChatUtil.sendMessage(sender, "&7Toggle pickup &coff &7for &e" + target.getName() + "&7!", true);
                        }
                    });
                }
            }, this.canSkipConfirmation("togglepickup", targets, sender));
        }, this.canSkipCooldown("togglepickup", "gorgon.essentials.togglepickup", targets, sender));

    }

    @Suggestions("states")
    public @NonNull List<String> statesSuggestion(final @NonNull CommandContext<CommandSender> context, final @NonNull String input) {
        return List.of("on", "off");
    }

}
