package io.inf8ty.playerthemes.command;

import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.MessagesSettings;
import io.inf8ty.playerthemes.theme.Theme;
import io.inf8ty.playerthemes.color.Text;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ThemesCommand implements TabExecutor {
    private final PlayerThemes plugin;
    private final Object2LongOpenHashMap<UUID> cooldowns = new Object2LongOpenHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        MessagesSettings messages = plugin.config().messages();

        if (strings.length == 0) {
            commandSender.sendMessage(Text.colorize(messages.help(), commandSender));
            return true;
        }

        if (commandSender instanceof Player player) {
            boolean isOther = strings.length >= 2;
            var settings = isOther ? plugin.config().command().selectOther() : plugin.config().command().select();

            boolean bypass = settings.cooldownBypass() && player.hasPermission("playerthemes.cooldown.bypass");
            long cd = settings.cooldown();

            if (!bypass && cd > 0) {
                long left = (cooldowns.getOrDefault(player.getUniqueId(), 0L) + cd - System.currentTimeMillis()) / 1000;
                if (left > 0) {
                    player.sendMessage(Text.colorize(messages.cooldown().replace("%time%", String.valueOf(left)), player));
                    return true;
                }
            }
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }

        String theme = strings[0].toLowerCase();

        if (strings.length >= 2) {
            if (!commandSender.hasPermission("playerthemes.install.other")) {
                commandSender.sendMessage(Text.colorize(messages.noPermission(), commandSender));
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(strings[1]);
            applyTheme(commandSender, target, theme);
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Text.colorize(messages.onlyPlayers(), commandSender));
            return true;
        }

        if (!player.hasPermission("playerthemes.install")) {
            player.sendMessage(Text.colorize(messages.noPermission(), player));
            return true;
        }

        applyTheme(player, player, theme);
        return true;
    }

    private void applyTheme(CommandSender sender, OfflinePlayer target, String theme) {
        var themeManager = plugin.themeManager();

        MessagesSettings messages = plugin.config().messages();
        boolean isSelf = sender.equals(target);

        if (theme.equals("reset")) {
            themeManager.resetTheme(target.getUniqueId());
            String message = isSelf ? messages.resetSuccess() : messages.resetSuccessPlayer().replace("%player%", target.getName());
            sender.sendMessage(Text.colorize(message, sender));
            return;
        }

        if (!themeManager.hasTheme(theme)) {
            sender.sendMessage(Text.colorize(messages.themeNotFound().replace("%theme%", theme), sender));
            return;
        }

        if (sender instanceof Player player && isSelf && !themeManager.hasPermissionToUse(player, theme)) {
            sender.sendMessage(Text.colorize(messages.themeNoPermission(), sender));
            return;
        }

        Theme themeObj = themeManager.theme(theme);
        themeManager.changeTheme(target.getUniqueId(), themeObj);

        if (isSelf) {
            sender.sendMessage(Text.colorize(messages.themeInstalled()
                    .replace("%theme%", themeObj.displayName()), sender));
        } else {
            sender.sendMessage(Text.colorize(messages.themeInstalledPlayer()
                    .replace("%theme%", themeObj.displayName())
                    .replace("%player%", target.getName()), sender));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        boolean canInstallSelf = commandSender.hasPermission("playerthemes.install");
        boolean canInstallOther = commandSender.hasPermission("playerthemes.install.other");

        if (strings.length == 1) {
            if (!canInstallSelf && !canInstallOther) return Collections.emptyList();

            List<String> completions = new ArrayList<>();
            completions.add("reset");

            for (Theme theme : plugin.themeManager().themes().values()) {
                if (!StringUtil.startsWithIgnoreCase(theme.id(), strings[0])) continue;

                if (canInstallOther || plugin.themeManager().hasPermissionToUse(commandSender, theme)) {
                    completions.add(theme.id());
                }
            }

            return completions;
        }
        if (strings.length == 2 && canInstallOther) {
            List<String> result = new ArrayList<>();
            String input = strings[1];

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (StringUtil.startsWithIgnoreCase(player.getName(), input)) {
                    result.add(player.getName());
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
