package me.superchirok1.playerthemes.manager.color.impl;

import me.superchirok1.playerthemes.manager.color.Colorizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageColorizer implements Colorizer {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('\u00A7')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    @Override
    public String colorize(String string) {
        if (string == null) return "";
        Component comp = MiniMessage.miniMessage().deserialize(string);
        return LEGACY.serialize(comp);
    }

}
