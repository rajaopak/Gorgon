package id.rajaopak.gorgon.listener;

import id.rajaopak.common.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.util.UUID;

public class ChatListener implements Listener {

    private final UUID online_uuid = UUID.fromString("85750130-ee4b-4bb4-a28b-c1125779a460");
    private final UUID offline_uuid = UUID.fromString("ecdeb677-1931-3797-94b9-89bdae8c3816");

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().getUniqueId().equals(online_uuid) || e.getPlayer().getUniqueId().equals(offline_uuid)) {
            e.setMessage(ChatUtil.color(e.getMessage()));

            Player p = e.getPlayer();
            if (e.getMessage().equalsIgnoreCase("test color anjay")) {
                p.sendMessage(ChatUtil.rgbGradient("Hello world with rgbGradient in linear!", Color.BLUE, Color.RED, ChatUtil::linear));
                p.sendMessage(ChatUtil.rgbGradient("Hello world with rgbGradient in quadratic false!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, false)));
                p.sendMessage(ChatUtil.rgbGradient("Hello world with rgbGradient in quadratic true!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, true)));
                p.sendMessage(ChatUtil.hsvGradient("Hello world with hsvGradient in linear!", Color.BLUE, Color.RED, ChatUtil::linear));
                p.sendMessage(ChatUtil.hsvGradient("Hello world with hsvGradient in quadratic false!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, false)));
                p.sendMessage(ChatUtil.hsvGradient("Hello world with hsvGradient in quadratic true!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, true)));
            }
        }
    }
}
