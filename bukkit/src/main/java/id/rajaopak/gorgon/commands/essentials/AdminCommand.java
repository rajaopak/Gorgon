package id.rajaopak.gorgon.commands.essentials;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.commands.BaseCommand;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.enums.FilterState;
import id.rajaopak.gorgon.module.helpme.gui.HelpMeGui;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AdminCommand extends BaseCommand {

    private final Gorgon plugin;

    public AdminCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("gorgon reload")
    @CommandPermission("gorgon.admin.reload")
    public void reloadCommand(final @NonNull CommandSender sender) {
        if (!PermissionChecker.check(sender, "admin.reload")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return;
        }

        this.plugin.getConfigFile().reloadConfig();
        ChatUtil.sendMessage(sender, LanguageFile.getPrefix() + " " + LanguageFile.getPluginReloading());
    }

    @CommandMethod("gorgon helpme history")
    @CommandPermission("gorgon.helpme.history")
    public void test(final @NonNull CommandSender sender) {
        if (!PermissionChecker.check(sender, "helpme.history")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return;
        }

        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer(), true);
            return;
        }

        HelpMeGui gui = new HelpMeGui(player);

        gui.helpMeHistoryPage(0, FilterState.ALL);
    }
}
