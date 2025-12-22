package me.superchirok1.playerthemes.config;

import me.superchirok1.playerthemes.config.values.ConfigValues;
import org.bukkit.configuration.ConfigurationSection;

public class Config {

    public ConfigValues get;

    public void init(ConfigurationSection s) {
        get = new ConfigValues(
                s.getBoolean("metrics", true),
                s.getString("colorizer", "minimessage"),
                s.getString("localization", "en"),
                s.getString("data.path", "data"),
                s.getString("permissions.reload", "playerthemes.reload"),
                s.getString("permissions.install", "playerthemes.install"),
                s.getString("permissions.install_other", "playerthemes.install.other"),
                s.getString("default_theme"),
                s.getConfigurationSection("themes")
        );
    }

}
