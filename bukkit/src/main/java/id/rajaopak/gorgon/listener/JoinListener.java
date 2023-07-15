package id.rajaopak.gorgon.listener;

import id.rajaopak.common.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;

public class JoinListener implements Listener {

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (e.getMessage().equals("test")) {
            p.sendMessage(ChatUtil.rgbGradient("Hello world with rgbGradient in linear!", Color.BLUE, Color.RED, ChatUtil::linear));
            p.sendMessage(ChatUtil.rgbGradient("Hello world with rgbGradient in quadratic false!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, false)));
            p.sendMessage(ChatUtil.rgbGradient("Hello world with rgbGradient in quadratic true!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, true)));
            p.sendMessage(ChatUtil.hsvGradient("Hello world with hsvGradient in linear!", Color.BLUE, Color.RED, ChatUtil::linear));
            p.sendMessage(ChatUtil.hsvGradient("Hello world with hsvGradient in quadratic false!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, false)));
            p.sendMessage(ChatUtil.hsvGradient("Hello world with hsvGradient in quadratic true!", Color.BLUE, Color.RED, (from, to, max) -> ChatUtil.quadratic(from, to, max, true)));
        }
    }

}
