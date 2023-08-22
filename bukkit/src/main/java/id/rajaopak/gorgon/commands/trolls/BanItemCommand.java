package id.rajaopak.gorgon.commands.trolls;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import de.tr7zw.nbtapi.NBTItem;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.common.utils.ItemBuilder;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.commands.BaseCommand;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.utils.PermissionChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class BanItemCommand extends BaseCommand {

    private final Gorgon plugin;

    public BanItemCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("banitem <args> [material]")
    @CommandPermission("gorgon.troll.banitem")
    public void setAndRemoveBanItemCommand(final @NonNull CommandSender sender, final @NonNull @Argument(value = "args", suggestions = "args") String args,
                                   final @Nullable @Argument("material") Material material) {
        if (!PermissionChecker.check(sender, "troll.banitemm")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return;
        }

        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer(), true);
            return;
        }

        switch (args) {
            case "set" -> {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    ChatUtil.sendMessage(sender, "&6Please held some item in your hand!", true);
                    return;
                }

                NBTItem item = new NBTItem(player.getInventory().getItemInMainHand());

                item.setBoolean("IsBanItem", true);
                item.applyNBT(player.getInventory().getItemInMainHand());
                ChatUtil.sendMessage(sender, "&7Successfully set ban item tag to this item!", true);
            }
            case "remove" -> {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    ChatUtil.sendMessage(sender, "&6Please held some item in your hand!", true);
                    return;
                }

                NBTItem item = new NBTItem(player.getInventory().getItemInMainHand());

                if (item.getBoolean("IsBanItem")) {
                    item.removeKey("IsBanItem");
                    ChatUtil.sendMessage(sender, "&7Successfully remove ban item tag from this item!", true);
                } else {
                    ChatUtil.sendMessage(sender, "&eNothing change, the item is not ban item!", true);
                }
            }
            default -> {
                this.plugin.getMinecraftHelp().queryCommands("banitem", sender);
            }
        }
    }

    @CommandMethod("banitem give [material] [player]")
    @CommandPermission("gorgon.troll.banitem")
    public void giveBanItemCommand(final @NonNull CommandSender sender, final @NonNull @Argument("material") Material material,
                                   final @Nullable @Argument(value = "player", suggestions = "players") String targetName) {
        if (!PermissionChecker.check(sender, "troll.banitemm")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission(), true);
            return;
        }

        if (targetName == null) {
            if (!(sender instanceof Player player)) {
                this.plugin.getMinecraftHelp().queryCommands("banitem", sender);
                return;
            }

            NBTItem item = new NBTItem(ItemBuilder.from(material).build());

            item.setBoolean("IsBanItem", true);

            player.getInventory().addItem(item.getItem());
            ChatUtil.sendMessage(sender, "&aGive you ban item with type &e" + item.getItem().getType().name() + "&a.", true);
        } else {
            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                ChatUtil.sendMessage(sender, LanguageFile.getPlayerNotFound(), true);
                return;
            }

            NBTItem item = new NBTItem(ItemBuilder.from(material).build());

            item.setBoolean("IsBanItem", true);

            target.getInventory().addItem(item.getItem());
            ChatUtil.sendMessage(sender, "&7Give &a" + target.getName() + " &7ban item with type &e" + item.getItem().getType().name() + "&7.", true);
            ChatUtil.sendMessage(sender, "&7You has been given ban item with type &e" + item.getItem().getType().name() + "&7.", true);
        }
    }

    @Suggestions("args")
    public List<String> argsSuggest(CommandContext<CommandSender> context, String input) {
        return List.of("give", "set", "remove");
    }

}
