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
                    Theme theme = new Theme(
                            section.getString("name", key),
                            section.getString("value", ""),
                            section.getString("permission"),
                            section.getString("description", "")
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
                ? theme.getName() : getClassicThemeName();
    }

    public String getPlayerThemeValue(@NotNull OfflinePlayer player) {
        Theme theme = themes.get(data.get(player.getUniqueId()));
        return theme != null
                ? theme.getValue() : getClassicThemeValue();
    }

    public String getPlayerThemeDescription(@NotNull OfflinePlayer player) {
        Theme theme = themes.get(data.get(player.getUniqueId()));
        return theme != null
                ? theme.getDescription() : getClassicThemeDescription();
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
                ? theme.getName() : "";
    }

    public String getClassicThemeValue() {
        Theme theme = themes.get(pl.config.get.defTheme());
        return theme != null
                ? theme.getValue() : "";
    }

    public String getClassicThemeDescription() {
        Theme theme = themes.get(pl.config.get.defTheme());
        return theme != null
                ? theme.getDescription() : "";
    }

    public boolean hasTheme(String theme) {
        return themes.containsKey(theme.toLowerCase());
    }

    public boolean hasPermissionToUse(Player player, String s) {
        Theme theme = themes.get(s);
        if (theme == null) return true;

        return theme.getPermission() == null
                || player.hasPermission(theme.getPermission());
    }

    public String getThemeName(@NotNull String id) {
        Theme theme = themes.get(id);
        return theme != null
                ? theme.getName() : null;
    }

    public String getThemeValue(@NotNull String id) {
        Theme theme = themes.get(id);
        return theme != null
                ? theme.getValue() : null;
    }

    public String getThemeDescription(@NotNull String id) {
        Theme theme = themes.get(id);
        return theme != null
                ? theme.getDescription() : null;
    }

    public void set(UUID uuid, String themeId) {
        themeId = themeId.toLowerCase();
        Player player = Bukkit.getPlayer(uuid);

        if (!themes.containsKey(themeId)) return;

        Theme themeClass = themes.get(themeId);

        if (player != null && themeClass.getPermission() != null && !player.hasPermission(themeClass.getPermission())) {
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
