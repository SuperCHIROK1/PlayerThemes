package io.inf8ty.playerthemes.color.impl;

import io.inf8ty.playerthemes.color.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageSerializer implements Serializer {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();

    @Override
    public String colorize(String text) {
        if (text == null || text.isEmpty()) return text;
        Component component = MINI_MESSAGE.deserialize(text);
        return LEGACY_COMPONENT_SERIALIZER.serialize(component);
    }
}
