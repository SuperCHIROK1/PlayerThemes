package me.superchirok1.playerthemes.service;

import me.superchirok1.playerthemes.PlayerThemes;
import me.superchirok1.playerthemes.manager.logger.Level;
import me.superchirok1.playerthemes.manager.logger.PluginLogger;
import me.superchirok1.playerthemes.model.Theme;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThemeService {

    private final PlayerThemes pl;

    private final Map<UUID, String> data = new HashMap<>();
    private final Map<String, Theme> themes = new HashMap<>();

    public Map<UUID, String> getData() {
        return data;
    }

    public Map<String, Theme> getThemes() {
        return themes;
    }

    public File file;
    public YamlConfiguration yaml;

    public ThemeService(PlayerThemes pl) {
        this.pl = pl;
    }

    public void init() {
        file = new File(pl.getDataFolder(), pl.config.get.dataPath() + ".yml");
        if (!file.exists()) {
            pl.saveResource(pl.config.get.dataPath() + ".yml", false);
        }
        yaml = YamlConfiguration.loadConfiguration(file);

        data.clear();
        ConfigurationSection dataSection = yaml.getConfigurationSection("data");
        if (dataSection != null) {
            for (String uuid : dataSection.getKeys(false)) {
                String theme = yaml.getString("data." + uuid);
                try {
                    data.put(UUID.fromString(uuid), theme);
                } catch (IllegalArgumentException e) {
                    PluginLogger.logBlock(Level.WARN, "Incorrect UUID in data.yml: " + uuid);
                }
            }
        }

        themes.clear();
        ConfigurationSection sec = pl.config.get.themes();
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                ConfigurationSection section = sec.getConfigurationSection(key);

                if (section != null) {

                    Map<String, String> valuesMap = null;

                    ConfigurationSection values = section.getConfigurationSection("values");
                    if (values!=null) {
                        valuesMap = new HashMap<>();
                        for (String valueKey : values.getKeys(false)) {
                            valuesMap.put(valueKey, values.getString(valueKey));
                        }
                    }

                    Theme theme = new Theme(
                            section.getString("name", key),
                            section.getString("value", ""),
                            section.getString("permission"),
                            section.getString("description", ""),
                            valuesMap
                    );
                    themes.put(key.toLowerCase(), theme);
                }
            }
        } else {
            PluginLogger.logBlock(Level.ERROR, "Themes not found in config.yml");
        }
    }

    public String getPlayerThemeId(@NotNull OfflinePlayer player) {
        return data.getOrDefault(player.getUniqueId(), getClassicThemeId());
    }

    public String getPlayerThemeName(@NotNull OfflinePlayer player) {
        Theme theme = themes.get(data.get(player.getUniqueId()));
        return theme != null
                ? theme.name() : getClassicThemeName();
    }

    public String getPlayerThemeValue(@NotNull OfflinePlayer player) {
        Theme theme = themes.get(data.get(player.getUniqueId()));
        return theme != null
                ? theme.value() : getClassicThemeValue();
    }

    public String getPlayerThemeDescription(@NotNull OfflinePlayer player) {
        Theme theme = themes.get(data.get(player.getUniqueId()));
        return theme != null
                ? theme.description() : getClassicThemeDescription();
    }

    public String getPlayerThemeValueByKey(@NotNull OfflinePlayer player, @NotNull String key) {
        Theme theme = themes.get(data.get(player.getUniqueId()));
        return theme != null
                ? theme.getValueByKey(key) : getClassicThemeValueByKey(key);
    }

    public Theme getPlayerTheme(@NotNull OfflinePlayer player) {
        return themes.get(data.get(player.getUniqueId()));
    }

    public String getClassicThemeId() {
        return pl.config.get.defTheme();
    }

    public String getClassicThemeName() {
        Theme theme = themes.get(pl.config.get.defTheme());
        return theme != null
                ? theme.name() : "";
    }

    public String getClassicThemeValue() {
        Theme theme = themes.get(pl.config.get.defTheme());
        return theme != null
                ? theme.value() : "";
    }

    public String getClassicThemeDescription() {
        Theme theme = themes.get(pl.config.get.defTheme());
        return theme != null
                ? theme.description() : "";
    }

    public String getClassicThemeValueByKey(@NotNull String key) {
        Theme theme = themes.get(pl.config.get.defTheme());
        return theme != null
                ? theme.getValueByKey(key) : "";
    }

    public boolean hasTheme(String theme) {
        return themes.containsKey(theme.toLowerCase());
    }

    public boolean hasPermissionToUse(Player player, String s) {
        Theme theme = themes.get(s);
        if (theme == null) return true;

        return theme.permission() == null
                || player.hasPermission(theme.permission());
    }

    public String getThemeName(@NotNull String id) {
        Theme theme = themes.get(id);
        return theme != null
                ? theme.name() : null;
    }

    public String getThemeValue(@NotNull String id) {
        Theme theme = themes.get(id);
        return theme != null
                ? theme.value() : null;
    }

    public String getThemeDescription(@NotNull String id) {
        Theme theme = themes.get(id);
        return theme != null
                ? theme.description() : null;
    }

    public String getThemeValueByKey(@NotNull String themeId, @NotNull String key) {
        Theme theme = themes.get(themeId);
        return theme != null
                ? theme.getValueByKey(key) : null;
    }

    public void set(UUID uuid, String themeId) {
        themeId = themeId.toLowerCase();
        Player player = Bukkit.getPlayer(uuid);

        if (!themes.containsKey(themeId)) return;

        Theme themeClass = themes.get(themeId);

        if (player != null && themeClass.permission() != null && !player.hasPermission(themeClass.permission())) {
            return;
        }

        yaml.set("data." + uuid, themeId);
        data.put(uuid, themeId);

        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            try {
                yaml.save(file);
            } catch (IOException e) {
                PluginLogger.logBlock(Level.ERROR, "Could not save data.yml");
            }
        });
    }

}
