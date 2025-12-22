package me.superchirok1.playerthemes.manager.logger;

public enum Level {
    INFO(""),
    ERROR("&c"),
    SUCCESS("&x&7&3&F&F&0&0"),
    WARN("&e");

    final String color;

    Level(String color) {
        this.color = color;
    }
}
