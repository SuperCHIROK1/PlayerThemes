package me.superchirok1.playerthemes.manager.localization;

import me.superchirok1.playerthemes.PlayerThemes;
import me.superchirok1.playerthemes.config.Config;
import me.superchirok1.playerthemes.config.values.MessagesValues;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LocalizationProvider {

    private final PlayerThemes pl;

    private YamlConfiguration yaml;
    private File file;

    public MessagesValues get;

    private String prefix = "";

    public LocalizationProvider(PlayerThemes pl) {
        this.pl = pl;
    }

    public void init(Config config) {
        String fileName = "messages/messages_"+config.get.localization()+".yml";
        file = new File(pl.getDataFolder(), fileName);

        if (!file.exists()) {
            pl.saveResource(fileName, false);
        }

        yaml = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection conf = yaml.getConfigurationSection("messages");

        prefix = conf.getString("prefix", "");

        get = new MessagesValues(
                format(conf.getString("prefix", "")),
                format(conf.getString("no_perms", "You dont have permissions")),
                format(conf.getString("admin.reloaded", "Reloaded")),
                format(conf.getString("command.player_not_found", "Player not found")),
                format(conf.getString("command.theme_installed", "Theme %s intalled")),
                format(conf.getString("command.theme_not_found", "Theme %s not found")),
                format(conf.getString("command.player_only", "Player only! (not console command)")),
                format(conf.getString("command.theme_no_perms", "You dont have permissions")),
                format(conf.getString("command.usage", "/themes select <theme> <?player>"))
        );

    }

    private String format(String text) {
        return pl.colorizer.get.colorize(text.replace("%prefix%", prefix));
    }

}
