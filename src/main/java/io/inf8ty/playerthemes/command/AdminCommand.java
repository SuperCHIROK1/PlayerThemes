package io.inf8ty.playerthemes.command;

import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.MessagesSettings;
import io.inf8ty.playerthemes.color.Text;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AdminCommand implements TabExecutor {
    private final PlayerThemes plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        MessagesSettings messages = plugin.config().messages();

        if (!commandSender.hasPermission("playerthemes.admin")) {
            commandSender.sendMessage(Text.colorize(messages.noPermission(), commandSender));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(Text.colorize(messages.adminHelp(), commandSender));
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "reload", "r": {
                plugin.reload();
                commandSender.sendMessage(Text.colorize(messages.adminReloaded(), commandSender));
                return true;
            }
            case "migrate": {
                if (strings.length < 3) {
                    commandSender.sendMessage(Text.colorize(messages.adminHelp()));
                    return true;
                }
                String fileName = strings[2];
                boolean force = strings.length > 3 && (
                        strings[3].equalsIgnoreCase("-f") ||
                                strings[3].equalsIgnoreCase("--force")
                );
                File file = new File(plugin.getDataFolder(), fileName);
                if (!file.exists()) {
                    commandSender.sendMessage(Text.colorize(messages.adminMigrationFileNotFound()
                            .replace("%file%", fileName), commandSender));
                    return true;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection section = config.getConfigurationSection("data");
                int count = 0;
                for (String key : section.getKeys(false)) {
                    UUID uuid = UUID.fromString(key);
                    String theme = section.getString(key);
                    if (!force && !plugin.themeManager().hasTheme(theme))
                        continue;
                    plugin.themeManager().changeTheme(uuid, theme);
                    count++;
                }
                commandSender.sendMessage(Text.colorize(messages.adminMigrationSuccess()
                        .replace("%count%", String.valueOf(count)), commandSender));
                return true;
            }
            default: {
                commandSender.sendMessage(Text.colorize(messages.adminHelp()));
                return true;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("playerthemes.admin")) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], List.of("reload", "migrate"), completions);
        }

        if (strings[0].equalsIgnoreCase("migrate")) {
            if (strings.length == 2)
                return StringUtil.copyPartialMatches(strings[1], List.of("data"), completions);

            if (strings.length == 3) {
                File folder = plugin.getDataFolder();
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
                if (files != null) {
                    List<String> fileNames = new ArrayList<>();
                    for (File file : files) {
                        fileNames.add(file.getName());
                    }
                    return StringUtil.copyPartialMatches(strings[2], fileNames, completions);
                }
            }

            if (strings.length == 4)
                return StringUtil.copyPartialMatches(strings[3], List.of("-f", "--force"), completions);
        }

        return Collections.emptyList();
    }
}
