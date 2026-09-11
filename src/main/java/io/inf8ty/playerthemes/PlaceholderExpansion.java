package io.inf8ty.playerthemes;

import io.inf8ty.playerthemes.theme.Theme;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    private final PlayerThemes plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "pth";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SuperCHIROK1";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        var themeManager = plugin.themeManager();

        int lastIndex = params.lastIndexOf('_');
        if (lastIndex > 0) {
            String potentialTheme = params.substring(lastIndex + 1);
            if (themeManager.hasTheme(potentialTheme)) {
                String key = params.substring(0, lastIndex);
                return themeManager.theme(potentialTheme).value(key);
            }
        }

        Theme theme = themeManager.themeByPlayer(player.getUniqueId());

        if (!themeManager.config().checkPermissionOnlyOnSelect()) {
            Player onlinePlayer = player.getPlayer();

            if (onlinePlayer != null && !onlinePlayer.hasPermission(theme.permission())) {
                themeManager.resetTheme(player.getUniqueId());
                theme = themeManager.defaultTheme();
            }
        }

        String result = theme.value(params);
        return result != null ? result : "";
    }
}
