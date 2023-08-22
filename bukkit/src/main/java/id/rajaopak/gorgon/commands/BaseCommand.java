package id.rajaopak.gorgon.commands;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.structure.CanSkipConfirmation;
import id.rajaopak.gorgon.structure.CanSkipCooldown;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class BaseCommand {

    public TargetsCallback getTargets(CommandSender sender, @Nullable String arg) {
        TargetsCallback callback = new TargetsCallback();
        if (sender instanceof Player) {
            if (arg == null || arg.isEmpty()) {
                callback.add((Player) sender);
                return callback;
            }

            switch (arg.toLowerCase()) {
                case "self" -> {
                    callback.add((Player) sender);
                    return callback;
                }
                case "*", "@a" -> {
                    callback.addAll(Bukkit.getOnlinePlayers());
                    return callback;
                }
            }

            Player target = Bukkit.getPlayer(arg);

            if (target == null) {
                ChatUtil.sendMessage(sender, LanguageFile.getPlayerNotFound(), true);
                callback.setNotified(true);
                return callback;
            }

            callback.add(target);
            return callback;
        }

        if (arg == null || arg.isEmpty()) {
            ChatUtil.sendMessage(sender, LanguageFile.getSpecifyPlayer(), true);
            callback.setNotified(true);
            return callback;
        }

        switch (arg.toLowerCase()) {
            case "*", "@a" -> {
                callback.addAll(Bukkit.getOnlinePlayers());
                return callback;
            }
        }

        Player target = Bukkit.getPlayer(arg);

        if (target == null) {
            ChatUtil.sendMessage(sender, LanguageFile.getPlayerNotFound(), true);
            callback.setNotified(true);
            return callback;
        }

        callback.add(target);
        return callback;
    }

    public CanSkipConfirmation canSkipConfirmation(String action, TargetsCallback targets, CommandSender sender) {
        if (!Gorgon.getInstance().getConfigFile().isUseConfirmation()) {
            return new CanSkipConfirmation(sender, true, null);
        }

        if (targets.size() == 1) {
            Player target = targets.getTargets().stream().findFirst().orElse(null);
            if (target != null && target.equals(sender)) {
                return new CanSkipConfirmation(sender, true, null);
            }
        }

        if (targets.size() >= Gorgon.getInstance().getConfigFile().getMinConfirmation()) {
            return new CanSkipConfirmation(sender, false,
                    List.of("&7Are you sure want to execute &e" + action + " &7on &a" + targets.size() + " &7players?"));
        }

        return new CanSkipConfirmation(sender, false, List.of(""));
    }

    public CanSkipCooldown canSkipCooldown(String action, String permission, TargetsCallback targets, CommandSender sender) {
        if (Gorgon.getInstance().getConfigFile().isUseCooldown()) {
            return new CanSkipCooldown(sender, action, true, Gorgon.getInstance().getConfigFile().getCooldownTime(), null, null);
        }

        if (targets.size() >= Gorgon.getInstance().getConfigFile().getMinConfirmation()) {
            return new CanSkipCooldown(sender, action, false, Gorgon.getInstance().getConfigFile().getCooldownTime(), permission, List.of("&cYou are still in cooldown!"));
        }

        return new CanSkipCooldown(sender, "none", false, 0, null, null);
    }

    @Suggestions("player")
    public List<String> player(CommandContext<CommandSender> context, String input) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(input.toLowerCase())).sorted().collect(Collectors.toList());
    }

    @Suggestions("players")
    public List<String> players(CommandContext<CommandSender> context, String input) {
        List<String> list = new ArrayList<>();

        list.add("*");
        list.add("@a");
        list.add("@all");
        list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());

        return list.stream().filter(s -> s.toLowerCase().startsWith(input.toLowerCase())).sorted().collect(Collectors.toList());
    }

    @Data
    protected static class TargetsCallback {
        private boolean notified = false;
        private final Set<Player> targets = new HashSet<>();

        public void add(Player player) {
            this.targets.add(player);
        }

        public void addAll(Collection<? extends Player> player) {
            this.targets.addAll(player);
        }

        public void setNotified(boolean notified) {
            this.notified = notified;
        }

        public int size() {
            return this.targets.size();
        }

        public boolean isEmpty() {
            return this.targets.isEmpty();
        }

        public boolean notifyIfEmpty() {
            return this.isEmpty() && !this.isNotified();
        }

        public boolean isOthers(CommandSender sender) {
            return targets.size() > 1 || (sender instanceof Player && doesNotContain((Player) sender));
        }

        public boolean doesNotContain(Player player) {
            return !this.targets.contains(player);
        }

        public Stream<Player> stream() {
            return StreamSupport.stream(Spliterators.spliterator(targets, 0), false);
        }

        public void forEach(Consumer<? super Player> action) {
            for (Player target : targets) {
                action.accept(target);
            }
        }
    }
}
