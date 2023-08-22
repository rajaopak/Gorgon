package id.rajaopak.gorgon.commands;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class MainCommand extends BaseCommand {

    private final Gorgon plugin;

    public MainCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("gorgon")
    @CommandPermission("gorgon.use")
    public void mainCommand(final @NonNull CommandSender sender) {
        if (!PermissionChecker.check(sender, "use")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        this.plugin.getMinecraftHelp().queryCommands("", sender);
    }

    @CommandMethod("gorgon help [query]")
    @CommandPermission("gorgon.help")
    public void helpCommand(final @NonNull CommandSender sender, final @Argument(value = "query", suggestions = "help") @Greedy String query) {
        if (!PermissionChecker.check(sender, "help")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        this.plugin.getMinecraftHelp().queryCommands(query == null ? "" : query, sender);
    }

    @CommandMethod("gorgon confirm")
    @CommandPermission("gorgon.help")
    public void confirmCommand(final @NonNull CommandSender sender) {
        if (!PermissionChecker.check(sender, "confirm")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        if (sender instanceof Player p) {
            this.plugin.getConfirmationManager().confirm(p);
        } else {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer(), true);
        }
    }

    @CommandMethod("gorgon decline")
    @CommandPermission("gorgon.help")
    public void declineCommand(final @NonNull CommandSender sender) {
        if (!PermissionChecker.check(sender, "confirm")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        if (sender instanceof Player p) {
            this.plugin.getConfirmationManager().decline(p);
        } else {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer(), true);
        }
    }

    @Suggestions("help")
    public @NonNull List<String> suggestHelpQueries(final @NonNull CommandContext<CommandSender> context, final @NonNull String input) {
        return this.plugin.getManager().createCommandHelpHandler().queryRootIndex(context.getSender()).getEntries().stream()
                .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                .collect(Collectors.toList());
    }

}
