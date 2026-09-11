package io.inf8ty.playerthemes.config.section;

import org.bukkit.configuration.ConfigurationSection;

public record MessagesSettings(
        String noPermission,
        String adminReloaded,
        String adminHelp,
        String cooldown,
        String help,
        String onlyPlayers,
        String themeNotFound,
        String themeNoPermission,
        String themeInstalled,
        String themeInstalledPlayer,
        String resetSuccess,
        String resetSuccessPlayer,
        String adminMigrationFileNotFound,
        String adminMigrationSuccess
) {
    public static MessagesSettings from(ConfigurationSection section) {
        String prefix = section.getString("prefix");
        return new MessagesSettings(
                applyPrefix(section.getString("no-permission", ""), prefix),
                applyPrefix(section.getString("admin.reloaded", ""), prefix),
                applyPrefix(section.getString("admin.help", ""), prefix),
                applyPrefix(section.getString("cooldown", ""), prefix),
                applyPrefix(section.getString("theme.help", ""), prefix),
                applyPrefix(section.getString("only-players", ""), prefix),
                applyPrefix(section.getString("theme.not-found", ""), prefix),
                applyPrefix(section.getString("theme.no-permission", ""), prefix),
                applyPrefix(section.getString("theme.installed", ""), prefix),
                applyPrefix(section.getString("theme.installed-player", ""), prefix),
                applyPrefix(section.getString("theme.reset-success", ""), prefix),
                applyPrefix(section.getString("theme.reset-success-player", ""), prefix),
                applyPrefix(section.getString("admin.migration.file-not-found", ""), prefix),
                applyPrefix(section.getString("admin.migration.success", ""), prefix)
        );
    }

    private static String applyPrefix(String text, String prefix) {
        return text.replace("%prefix%", prefix);
    }
}
