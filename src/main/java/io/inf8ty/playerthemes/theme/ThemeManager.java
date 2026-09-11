package io.inf8ty.playerthemes.theme;

import io.inf8ty.playerthemes.PlayerThemes;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Data
@Accessors(fluent = true)
public class ThemeManager {
    private final Object2ObjectOpenHashMap<String, Theme> themes = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<UUID, String> players = new Object2ObjectOpenHashMap<>();

    private final PlayerThemes plugin;

    private Config config;

    public void initialize() {
        themes.clear();
        File file = new File(plugin.getDataFolder(), "themes.yml");
        if (!file.exists()) {
            plugin.saveResource( "themes.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String defaultTheme = config.getString("default-theme");
        if (defaultTheme == null) {
            plugin.getLogger().severe("Дефолтная тема (default-theme в themes.yml) не указана! Темы не будут загружены.");
            return;
        }
        this.config = new Config(
                config.getBoolean("check-permission-only-on-select", false),
                defaultTheme
        );
        ConfigurationSection section = config.getConfigurationSection("themes");
        if (section == null) {
            plugin.getLogger().severe("Нет секции `themes` в themes.yml! Темы не будут загружены.");
            return;
        }
        for (String key : section.getKeys(false)) {
            Theme theme = Theme.of(section.getConfigurationSection(key), plugin.getLogger(), key);
            if (theme != null) {
                themes.put(key, theme);
            }
        }
    }

    public record Config(
            boolean checkPermissionOnlyOnSelect,
            String defaultTheme
    ) {}

    public Theme theme(String id) {
        return themes.get(id);
    }

    public Theme themeByPlayer(UUID uuid) {
        return Optional.ofNullable(themes.get(players.get(uuid))).orElse(themes.get(config.defaultTheme()));
    }

    public void changeTheme(UUID uuid, Theme theme) {
        players.put(uuid, theme.id());
        plugin.database().save(uuid, theme.id());
        if (plugin.redisManager() != null) {
            plugin.redisManager().publish(uuid, theme.id());
        }
    }

    public void changeTheme(UUID uuid, String theme) {
        changeTheme(uuid, themes.get(theme));
    }

    public void resetTheme(UUID uuid) {
        players.remove(uuid);
        plugin.database().remove(uuid);
    }

    public boolean hasTheme(String theme) {
        return themes.containsKey(theme);
    }

    public boolean hasPermissionToUse(CommandSender sender, Theme theme) {
        if (theme.permission().isEmpty()) return true;
        if (themeIsDefault(theme)) return true;
        return sender.hasPermission(theme.permission());
    }

    public boolean hasPermissionToUse(CommandSender sender, String theme) {
        return hasPermissionToUse(sender, themes.get(theme));
    }

    public boolean themeIsDefault(Theme theme) {
        return config.defaultTheme().equals(theme.id());
    }

    public Theme defaultTheme() {
        return theme(config.defaultTheme());
    }
}
