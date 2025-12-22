package me.superchirok1.playerthemes.manager.logger;

import me.superchirok1.playerthemes.manager.color.impl.LegacyColorizer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class PluginLogger {

    private static final String prefix = "&7(&bPlayerThemes&7) &f";

    public static void log(Level level, String text) {
        send(prefix + level.color + text);
    }

    public static void logBlock(Level level, String text) {
        send("\n\n" + prefix + level.color + text + "\n");
    }

    private static void send(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }

}
