package io.inf8ty.playerthemes.theme;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Logger;

public record Theme(
        String id,
        String displayName,
        String value,
        String permission,
        String description,
        Object2ObjectOpenHashMap<String, String> values
) {
    public String value(String key) {
        return values.get(key);
    }

    public static Theme of(ConfigurationSection section, Logger logger, String id) {
        if (section == null) {
            logger.warning("Тема '" + id + "' не может быть загружена: секция темы отсутствует.");
            return null;
        }

        Object2ObjectOpenHashMap<String, String> values = new Object2ObjectOpenHashMap<>();

        String displayName = section.getString("display-name");
        if (displayName == null) displayName = section.getString("name", "");

        String value = section.getString("value", "");
        String permission = section.getString("permission", "");
        String description = section.getString("description", "");

        values.put("id", id);
        values.put("name", displayName);
        values.put("display_name", displayName);
        values.put("value", value);
        values.put("permission", permission);
        values.put("description", description);

        ConfigurationSection valuesSection = section.getConfigurationSection("values");
        if (valuesSection != null) {
            for (String key : valuesSection.getKeys(false)) {
                values.put(key, valuesSection.getString(key, ""));
            }
        }

        return new Theme(
                id,
                displayName,
                value,
                permission,
                description,
                values
        );
    }
}
