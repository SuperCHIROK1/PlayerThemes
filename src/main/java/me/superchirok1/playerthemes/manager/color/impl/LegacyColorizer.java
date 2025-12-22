package me.superchirok1.playerthemes.manager.color.impl;

import me.superchirok1.playerthemes.manager.color.Colorizer;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyColorizer implements Colorizer {

    private static final Pattern PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public String colorize(String string) {
        if (string == null) return "";

        Matcher matcher = PATTERN.matcher(string);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String color = ChatColor.of("#" + hex).toString();
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(color));
        }

        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

}
