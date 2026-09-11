package io.inf8ty.playerthemes.config;

import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.CommandSettings;
import io.inf8ty.playerthemes.config.section.DatabaseSettings;
import io.inf8ty.playerthemes.config.section.MessagesSettings;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
@Data
@Accessors(fluent = true)
public class MainConfig {
    private final PlayerThemes plugin;

    private boolean notifyUpdates = true;

    private DatabaseSettings database;
    private CommandSettings command;
    private MessagesSettings messages;

    public void load() {
        FileConfiguration config = plugin.getConfig();
        this.notifyUpdates = config.getBoolean("notify-updates", true);

        ConfigurationSection database = config.getConfigurationSection("database-settings");
        if (database == null) {
            database = config.createSection("database-settings");
        }

        ConfigurationSection sqlite = database.getConfigurationSection("sqlite");
        ConfigurationSection mysql = database.getConfigurationSection("mysql");

        ConfigurationSection redis = database.getConfigurationSection("redis");
        DatabaseSettings.RedisSettings redisSettings;
        if (!redis.getBoolean("enabled", false)) {
            redisSettings = null;
        } else {
            redisSettings = new DatabaseSettings.RedisSettings(
                    redis.getString("server-id"),
                    redis.getString("channel"),
                    redis.getString("connection.host"),
                    redis.getInt("connection.port"),
                    redis.getString("connection.username"),
                    redis.getString("connection.password"),
                    redis.getInt("connection.database")
            );
        }

        this.database = new DatabaseSettings(
                DatabaseSettings.Type.parse(database.getString("type", "SQLITE")),
                sqlite.getString("file-path", "data.db"),
                mysql.getString("host", "127.0.0.1"),
                mysql.getInt("port", 3306),
                mysql.getString("username", "root"),
                mysql.getString("password", ""),
                mysql.getString("database", "playerthemes"),
                mysql.getString("arguments", "?useSSL=false&autoReconnect=true&characterEncoding=utf8"),
                database.getConfigurationSection("pool-settings"),
                redisSettings
        );

        ConfigurationSection commands = config.getConfigurationSection("command-settings");
        if (commands == null) {
            commands = config.createSection("command-settings");
        }

        ConfigurationSection select = commands.getConfigurationSection("select");
        ConfigurationSection selectOther = commands.getConfigurationSection("select-other");

        this.command = new CommandSettings(
                commands.getString("main-command"),
                commands.getStringList("aliases"),
                CommandSettings.CooldownSettings.parseCooldown(select),
                CommandSettings.CooldownSettings.parseCooldown(selectOther)
        );

        this.messages = MessagesSettings.from(config.getConfigurationSection("messages"));
    }
}
