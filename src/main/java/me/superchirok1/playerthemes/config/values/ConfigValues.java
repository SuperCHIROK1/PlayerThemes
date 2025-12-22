package me.superchirok1.playerthemes.config.values;

import org.bukkit.configuration.ConfigurationSection;

public record ConfigValues(
    boolean metrics,
    String colorizer,
    String localization,
    String dataPath,
    String permReload,
    String permInstall,
    String permInstallOther,
    String defTheme,
    ConfigurationSection themes
) {}
