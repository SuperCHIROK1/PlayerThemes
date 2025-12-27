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
        return "1.0.2";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        var service = pl.service;

        switch (params) {
            case "name":
                return service.getPlayerThemeName(player);
            case "theme", "value":
                return service.getPlayerThemeValue(player);
            case "id":
                return service.getPlayerThemeId(player);
            case "desc":
                return service.getPlayerThemeDescription(player);
        }

        if (params.indexOf("_", 4)<7) {
            if (params.startsWith("values_")) {
                return service.getPlayerThemeValueByKey(player, params.substring(7));
            }
            if (params.startsWith("theme_") || params.startsWith("value_")) {
                return service.getThemeValue(params.substring(6));
            }
            if (params.startsWith("name_")) {
                return service.getThemeName(params.substring(5));
            }
            if (params.startsWith("desc_")) {
                return service.getThemeDescription(params.substring(5));
            }
        }

        return null;
    }

}
