package me.superchirok1.playerthemes;

import me.superchirok1.playerthemes.command.PlayerThemesCommand;
import me.superchirok1.playerthemes.config.Config;
import me.superchirok1.playerthemes.manager.color.TextColorizer;
import me.superchirok1.playerthemes.manager.localization.LocalizationProvider;
import me.superchirok1.playerthemes.manager.logger.Level;
import me.superchirok1.playerthemes.manager.logger.PluginLogger;
import me.superchirok1.playerthemes.placeholder.PAPIExpansion;
import me.superchirok1.playerthemes.service.ThemeService;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class PlayerThemes extends JavaPlugin {

    public Config config;
    public TextColorizer colorizer;
    public ThemeService service;

    public LocalizationProvider localeProvider;
    private final List<String> languageList = Arrays.asList("en","ru");


    @Override
    public void onEnable() {
        saveDefaultConfig();
        messagesSave();

        config = new Config();
        colorizer = new TextColorizer();
        service = new ThemeService(this);
        localeProvider = new LocalizationProvider(this);

        config.init(getConfig().getConfigurationSection("settings"));
        colorizer.init(config);
        service.init();
        localeProvider.init(config);

        commandRegister();
        new PAPIExpansion(this).register();
        PluginLogger.logBlock(Level.SUCCESS, "PlayerThemes enabled");

        if (config.get.metrics()) {
            Metrics metrics = new Metrics(this, 28488);
        }
    }

    @Override
    public void onDisable() {
        PluginLogger.logBlock(Level.ERROR, "PlayerThemes disabled");
    }

    private void messagesSave() {
        for (String language : languageList)
            saveResource("messages/messages_"+language+".yml", false);
    }

    private void commandRegister() {
        PlayerThemesCommand cmd = new PlayerThemesCommand(this);
        getCommand("themes").setExecutor(cmd);
        getCommand("themes").setTabCompleter(cmd);
    }

    public void reload() {
        reloadConfig();
        config.init(getConfig().getConfigurationSection("settings"));
        colorizer.init(config);
        service.init();
        localeProvider.init(config);
    }

}
