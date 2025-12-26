package me.superchirok1.playerthemes.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.superchirok1.playerthemes.PlayerThemes;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    private final PlayerThemes pl;

    public PAPIExpansion(PlayerThemes pl) {
        this.pl = pl;
    }

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
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("id")) {
            return pl.service.getPlayerThemeId(player);
        }

        if (params.equalsIgnoreCase("name")) {
            return pl.service.getPlayerThemeName(player);
        }

        if (params.equalsIgnoreCase("theme") || params.equalsIgnoreCase("value")) {
            return pl.service.getPlayerThemeValue(player);
        }

        if (params.equalsIgnoreCase("desc")) {
            return pl.service.getPlayerThemeDescription(player);
        }

        if (params.contains("_")) {
            if (params.startsWith("theme_") || params.startsWith("value_")) {
                params = params.replace("theme_", "")
                        .replace("value_", "");
                return pl.service.getThemeValue(params);
            }

            if (params.startsWith("desc_")) {
                params = params.replace("desc_", "");
                return pl.service.getThemeDescription(params);
            }

            if (params.startsWith("name_")) {
                params = params.replace("name_", "");
                return pl.service.getThemeName(params);
            }
        }

        return null;
    }

}
