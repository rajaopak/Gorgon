package id.rajaopak.gorgon.module.helpme.gui;

import id.rajaopak.common.gui.GuiBuilder;
import id.rajaopak.common.utils.ChatSession;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.common.utils.ItemBuilder;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.enums.FilterState;
import id.rajaopak.gorgon.enums.HelpMeState;
import id.rajaopak.gorgon.module.helpme.HelpMeData;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public class HelpMeGui {

    private final Player player;

    private GuiBuilder gui;

    public HelpMeGui(Player player) {
        this.player = player;
    }

    public void helpMePage() {
        this.gui = new GuiBuilder(27, "&2HelpMe &8- &2Call staff to help you!");

        this.gui.setFilterItem(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build());

        this.gui.setItem(13, ItemBuilder.from(Material.ANVIL).setName("&eTell us what's wrong?").setLore("&7Click here to start typing!").build(), event -> {
            setHelpMeReason().thenAccept(message -> {
                UUID helpMeUUID = UUID.randomUUID();
                HelpMeData data = new HelpMeData(helpMeUUID, this.player.getUniqueId(), this.player.getName(), message, Instant.now(), Gorgon.getServerName());

                Gorgon.getInstance().getHelpMeManager().addHelpMe(helpMeUUID, data);
                ChatUtil.sendMessagee(Gorgon.getInstance().getAudiences().sender(this.player), List.of(
                        ChatUtil.colors("&aPlease wait a moment until our staff response to your HelpMe!"),
                        ChatUtil.colors(text("You're HelpMe has been created with id: ").color(NamedTextColor.GREEN)),
                        ChatUtil.colors(text(helpMeUUID + " (click to copy)").color(NamedTextColor.GRAY)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, helpMeUUID.toString()))
                                .hoverEvent(HoverEvent.showText(ChatUtil.colors("&eClick to copy!")))
                        )));
                Gorgon.getInstance().getServer().getScheduler().runTask(Gorgon.getInstance(), () -> Gorgon.getInstance().getStaffHelpMeManager().sendHelpMeToStaff(data));
            });
        });

        this.gui.open(this.player);
    }

    public void acceptHelpMePage(@NotNull HelpMeData data) {
        this.gui = new GuiBuilder(45, "&2HelpMe from &6" + data.getSenderName());

        this.gui.setFilterItem(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build());

        this.gui.setItem(13, ItemBuilder.from(Material.WRITABLE_BOOK).setName("&2HelpMe from &e" + data.getSenderName())
                .setLore(
                        "&aHelpMe-Id: &7" + data.getHelpMeUUID(),
                        "&aSender-Id: &7" + data.getSenderUUID(),
                        "&aSender-Name: &7" + data.getSenderName(),
                        "&aSend-Time: &7" + DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(Timestamp.from(data.getSendTime()).toLocalDateTime()),
                        "&aMessage: &7")
                .addLore(ChatUtil.wordWrap("&7" + data.getMessage(), 25)).build());
        ChatUtil.sendMessage(player, "size: " + ChatUtil.wordWrap("&7" + data.getMessage(), 25).size());

        this.gui.setItem(30, ItemBuilder.from(Material.GREEN_WOOL).setName("&aAccept request").build(), event -> {
            this.gui.unCloseable(false);
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setStaffUUID(this.player.getUniqueId());
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setStaffName(this.player.getName());
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setAcceptedTime(Instant.now());
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setState(HelpMeState.ACCEPTED);
            Gorgon.getInstance().getHelpMeManager().notifyPlayerHelpMe(this.player, data);
            this.gui.close(this.player);
            Gorgon.getInstance().getStaffHelpMeManager().addStaffInHelpMe(this.player.getUniqueId(), Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()));
            Gorgon.getInstance().getDatabase().updateHelpMeData(data);
        });
        this.gui.setItem(32, ItemBuilder.from(Material.RED_WOOL).setName("&cDecline request").build(), event -> {
            this.gui.unCloseable(false);
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setStaffUUID(this.player.getUniqueId());
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setStaffName(this.player.getName());
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setAcceptedTime(Instant.now());
            Gorgon.getInstance().getHelpMeManager().getHelpMe(data.getSenderUUID()).setState(HelpMeState.DECLINED);
            Gorgon.getInstance().getHelpMeManager().notifyPlayerHelpMe(this.player, data);
            Gorgon.getInstance().getHelpMeManager().removeHelpMe(data.getSenderUUID());
            this.gui.close(this.player);
            Gorgon.getInstance().getDatabase().updateHelpMeData(data);
        });

        this.gui.open(this.player);
    }

    public void helpMeHistoryPage(int page, FilterState filter) {
        this.gui = new GuiBuilder(54, "&2HelpMe History (&e" + page + "&2)");

        this.gui.setHeaderAndFooter(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build());

        this.gui.setItem(49, ItemBuilder.from(Material.BARRIER).setName("&cClose").build(), event -> this.gui.close(this.player));
        this.gui.setItem(52, ItemBuilder.from(Material.HOPPER).setName("&eFilter").setLore(
                filter == FilterState.ALL ? "&e\u2022 ALL" : "&7\u2022 ALL",
                filter == FilterState.STATE ? FilterState.STATE.getObject().equalsIgnoreCase(HelpMeState.WAITING.getStatus()) ? "&e\u2022 WAITING" : "&7\u2022 WAITING" : "&7\u2022 WAITING",
                filter == FilterState.STATE ? FilterState.STATE.getObject().equalsIgnoreCase(HelpMeState.ACCEPTED.getStatus()) ? "&e\u2022 ACCEPTED" : "&7\u2022 ACCEPTED" : "&7\u2022 ACCEPTED",
                filter == FilterState.STATE ? FilterState.STATE.getObject().equalsIgnoreCase(HelpMeState.DECLINED.getStatus()) ? "&e\u2022 DECLINED" : "&7\u2022 DECLINED" : "&7\u2022 DECLINED",
                filter == FilterState.STATE ? FilterState.STATE.getObject().equalsIgnoreCase(HelpMeState.FINISH.getStatus()) ? "&e\u2022 FINISH" : "&7\u2022 FINISH" : "&7\u2022 FINISH",
                "",
                "&7Left click to go up!",
                "&7Right click to go down!"
        ).build(), event -> {
            if (event.getClick() == ClickType.LEFT) {
                if (filter == FilterState.ALL) {
                    helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.FINISH.getStatus()));

                } else if (filter == FilterState.STATE) {
                    HelpMeState state = HelpMeState.fromString(FilterState.STATE.getObject());

                    if (state == HelpMeState.WAITING) {
                        helpMeHistoryPage(0, FilterState.ALL);
                    } else if (state == HelpMeState.ACCEPTED) {
                        helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.WAITING.getStatus()));
                    } else if (state == HelpMeState.DECLINED) {
                        helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.ACCEPTED.getStatus()));
                    } else if (state == HelpMeState.FINISH) {
                        helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.DECLINED.getStatus()));
                    }
                } else {
                    helpMeHistoryPage(0, FilterState.ALL);
                }
            } else if (event.getClick() == ClickType.RIGHT) {
                if (filter == FilterState.ALL) {
                    helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.WAITING.getStatus()));

                } else if (filter == FilterState.STATE) {
                    HelpMeState state = HelpMeState.fromString(FilterState.STATE.getObject());

                    if (state == HelpMeState.WAITING) {
                        helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.ACCEPTED.getStatus()));
                    } else if (state == HelpMeState.ACCEPTED) {
                        helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.DECLINED.getStatus()));
                    } else if (state == HelpMeState.DECLINED) {
                        helpMeHistoryPage(0, FilterState.STATE.setObject(HelpMeState.FINISH.getStatus()));
                    } else if (state == HelpMeState.FINISH) {
                        helpMeHistoryPage(0, FilterState.ALL);
                    }
                } else {
                    helpMeHistoryPage(0, FilterState.ALL);
                }
            }
        });

        this.gui.setItem(53, ItemBuilder.from(Material.ANVIL).setName("&eSearch by name/uuid").build(), event -> setHelpMeReason().thenAccept(s ->
            Gorgon.getInstance().getServer().getScheduler().runTask(Gorgon.getInstance(), () -> helpMeHistoryPage(0, FilterState.PLAYER.setObject(s)))));

        int maxPerPage = 36;
        int size = Gorgon.getInstance().getDatabase().sizeHelpMe(filter);

        if (page > 0 && size < (page * maxPerPage) + 1) {
            helpMeHistoryPage(page - 1, filter);
            return;
        }

        int max = (page * maxPerPage) + maxPerPage;
        int min = page * maxPerPage;

        if (max > size) {
            max = max - (max - size);
        }

        List<HelpMeData> dataList = Gorgon.getInstance().getDatabase().getHelpMeData(36, page * maxPerPage, filter).join().get();
        this.gui.setItem(4, ItemBuilder.from(Material.WRITABLE_BOOK).setName("&2HelpMe Stats").setLore(
                "&aTotal HelpMe: &7" + size,
                "&aTotal Page: &7" + (size / maxPerPage),
                "&aData showing: &7" + min + " - " + max,
                "&aFilter state: &7" + filter + " " + (filter.getObject() != null ? "(" + filter.getObject() + ")" : ""),
                "",
                "&eClick to refresh the page"
        ).build(), event -> helpMeHistoryPage(page, filter));

        // previous buttom
        if (page > 0) {
            this.gui.setItem(48, ItemBuilder.from(Material.ARROW).setName("&7Previous page").build(), event -> helpMeHistoryPage(page - 1, filter));
        }

        // next button
        if (!(size < ((page + 1) * maxPerPage))) {
            this.gui.setItem(50, ItemBuilder.from(Material.ARROW).setName("&7Next page").build(), event -> helpMeHistoryPage(page + 1, filter));
        }

        dataList.forEach(data -> this.gui.addItem(ItemBuilder.from(Material.PAPER)
                .setName("&2HelpMe of &e" + data.getSenderName())
                .setLore(
                        "&aHelpMe-Id: &7" + data.getHelpMeUUID(),
                        "&aSender-Id: &7" + data.getSenderUUID(),
                        "&aSender-Name: &7" + data.getSenderName(),
                        "&aSend-Time: &7" + DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(Timestamp.from(data.getSendTime()).toLocalDateTime()),
                        "&aStaff-Id: &7" + data.getStaffUUID(),
                        "&aStaff-Name: &7" + data.getStaffName(),
                        "&aAccepted-Time: &7" + (data.getAcceptedTime() == null ? "none" : DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(Timestamp.from(data.getAcceptedTime()).toLocalDateTime())),
                        "&aStatus: &7" + data.getState().getStatus(),
                        "&aServeer: &7" + data.getServerName(),
                        "&aMessage: &7")
                .addLore(ChatUtil.wordWrap("&7" + data.getMessage(), 25))
                .build(), e -> {
            this.gui.close(this.player);
            ChatUtil.sendMessage(Gorgon.getInstance().getAudiences().sender(this.player), text("[").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD)
                    .append(text("Click Here").color(NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, data.getHelpMeUUID().toString()))
                            .hoverEvent(HoverEvent.showText(ChatUtil.colors("&a" + data.getHelpMeUUID())))
                    ).append(text("]").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD)));
        }));

        this.gui.setFilterItem(ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).build());
        this.gui.open(this.player);
    }

    public void pendingRequestPage(int page) {

    }

    private @NotNull CompletableFuture<String> setHelpMeReason() {
        ChatSession session = new ChatSession();
        CompletableFuture<String> future = new CompletableFuture<>();

        this.gui.close(this.player);
        session.onComplete(complete -> ChatSession.Action.run(() -> future.complete(complete.message())))
                .plugin(Gorgon.getInstance()).open(this.player);

        return future;
    }
}
