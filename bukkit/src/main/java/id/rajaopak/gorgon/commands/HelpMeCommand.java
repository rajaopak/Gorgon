package id.rajaopak.gorgon.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.enums.FilterState;
import id.rajaopak.gorgon.enums.HelpMeState;
import id.rajaopak.gorgon.module.helpme.gui.HelpMeGui;
import id.rajaopak.gorgon.module.helpme.HelpMeData;
import id.rajaopak.gorgon.utils.PermissionChecker;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class HelpMeCommand extends BaseCommand {

    private final Gorgon plugin;

    public HelpMeCommand(Gorgon plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("helpme [message]")
    @CommandPermission("gorgon.helpme.use")
    public void mainCommand(@NonNull CommandSender sender, @Nullable @Greedy @Argument(value = "message") String message) {
        if (!PermissionChecker.check(sender, "helpme.use")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer());
            return;
        }

        if (message == null || message.isEmpty()) {
            HelpMeGui gui = new HelpMeGui(player);
            gui.helpMePage();
            return;
        }

//        if (this.plugin.getStaffHelpMeManager().checkIfStaff(player)) {
//            return;
//        }

        if (this.plugin.getHelpMeManager().containsPlayer(player.getUniqueId())) {
            ChatUtil.sendMessagee(Gorgon.getInstance().getAudiences().sender(player), List.of(
                    ChatUtil.colors(text("You already has HelpMe with id: ").color(NamedTextColor.RED)),
                    ChatUtil.colors(text( plugin.getHelpMeManager().getByPlayer(player.getUniqueId()).getHelpMeUUID() + " (click to copy)").color(NamedTextColor.GRAY)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,  plugin.getHelpMeManager().getByPlayer(player.getUniqueId()).getHelpMeUUID().toString()))
                            .hoverEvent(HoverEvent.showText(ChatUtil.colors("&eClick to copy!")))
                    )));
            return;
        }

        UUID helpMeUUID = UUID.randomUUID();
        HelpMeData data = new HelpMeData(helpMeUUID, player.getUniqueId(), player.getName(), message, Instant.now(), Gorgon.getServerName());

        this.plugin.getHelpMeManager().addHelpMe(helpMeUUID, data);
        ChatUtil.sendMessagee(Gorgon.getInstance().getAudiences().sender(player), List.of(
                ChatUtil.colors("&aPlease wait a moment until our staff response to your HelpMe!"),
                ChatUtil.colors(text("You're HelpMe has been created with id: ").color(NamedTextColor.GREEN)),
                ChatUtil.colors(text(helpMeUUID + " (click to copy)").color(NamedTextColor.GRAY)
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, helpMeUUID.toString()))
                        .hoverEvent(HoverEvent.showText(ChatUtil.colors("&eClick to copy!"))))));
        this.plugin.getStaffHelpMeManager().sendHelpMeToStaff(data);
        this.plugin.getDatabase().setHelpMeData(data);
    }

    @CommandMethod("helpme --finish")
    @CommandPermission("gorgon.helpme.staff")
    public void helpMeFinishCommand(@NonNull CommandSender sender) {
        if (!PermissionChecker.check(sender, "helpme.staff")) {
            ChatUtil.sendMessage(sender, LanguageFile.getNoPermission());
            return;
        }

        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer());
            return;
        }

        if (!this.plugin.getStaffHelpMeManager().containsStaff(player.getUniqueId())) {
            ChatUtil.sendMessage(sender, "&cYou don't have any active HelpMe to be finished!");
            return;
        }

        HelpMeData data = this.plugin.getStaffHelpMeManager().getHelpMe(player.getUniqueId());

        data.setState(HelpMeState.FINISH);

        this.plugin.getDatabase().updateHelpMeData(data);
        this.plugin.getHelpMeManager().removeHelpMe(data.getHelpMeUUID());
        this.plugin.getStaffHelpMeManager().removeStaff(player.getUniqueId());
        this.plugin.getHelpMeManager().notifyPlayerHelpMe(player, data);
    }

    @CommandMethod("testhelpme")
    public void test(@NonNull CommandSender sender) {

        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer());
            return;
        }

        HelpMeGui gui = new HelpMeGui(player);

        //this.plugin.getHelpMeManager().addHelpMe(data.getHelpMeUUID(), data);
        gui.helpMeHistoryPage(0, FilterState.ALL);
    }

    @CommandMethod("initializedatatodatabase")
    public void testDatabase(@NonNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            ChatUtil.sendMessage(sender, LanguageFile.getCommandOnlyPlayer());
            return;
        }

        HelpMeState[] states = {HelpMeState.WAITING, HelpMeState.ACCEPTED, HelpMeState.DECLINED, HelpMeState.FINISH};

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            HelpMeData data = new HelpMeData(UUID.randomUUID(), player.getUniqueId(), player.getName(),
                    "&aaaaaaaaaaaaaaaa&baaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaa&caaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaa&daaaaaaaaaaaaaaaaaaaaaa",
                    Instant.now(), Gorgon.getServerName());

            data.setStaffUUID(player.getUniqueId());
            data.setStaffName(player.getName());
            data.setAcceptedTime(Instant.now());
            data.setState(states[random.nextInt(4)]);

            this.plugin.getDatabase().setHelpMeData(data);
        }
    }

}
