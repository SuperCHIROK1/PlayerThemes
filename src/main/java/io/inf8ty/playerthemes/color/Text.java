package io.inf8ty.playerthemes.color;

import io.inf8ty.playerthemes.color.impl.LegacySerializer;
import io.inf8ty.playerthemes.color.impl.MiniMessageSerializer;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@UtilityClass
public class Text {
    private Serializer serializer = new LegacySerializer();

    private final LegacySerializer legacySerializer = new LegacySerializer();

    public Serializer.Type type;

    public void init(FileConfiguration config) {
        if (config == null) return;
        type = Serializer.Type.valueOf(config.getString("serializer", "LEGACY").toUpperCase());
        serializer = type == Serializer.Type.LEGACY
                ? new LegacySerializer()
                : new MiniMessageSerializer();
    }

    public String colorize(String text) {
        return serializer.colorize(text);
    }

    public String colorize(String text, CommandSender sender) {
        if (sender instanceof Player player) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return colorize(text);
    }

    public String legacy(String text) {
        return legacySerializer.colorize(text);
    }
}
