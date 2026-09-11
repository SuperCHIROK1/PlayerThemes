package io.inf8ty.playerthemes.command;

import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.MessagesSettings;
import io.inf8ty.playerthemes.color.Text;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class AdminCommand implements TabExecutor {
    private final PlayerThemes plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        MessagesSettings messages = plugin.config().messages();

        if (!commandSender.hasPermission("playerthemes.admin")) {
            commandSender.sendMessage(messages.noPermission());
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(Text.colorize(messages.adminHelp()));
            return true;
        }
        if (strings[0].equalsIgnoreCase("reload") || strings[0].equalsIgnoreCase("r")) {
            plugin.reload();
            commandSender.sendMessage(Text.colorize(messages.adminReloaded()));
            return true;
        }
        commandSender.sendMessage(Text.colorize(messages.adminHelp()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("playerthemes.admin")) {
            return Collections.emptyList();
        }
        return List.of("reload");
    }
}
