package io.inf8ty.playerthemes.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.DatabaseSettings;
import io.inf8ty.playerthemes.database.AbstractDatabase;
import io.inf8ty.playerthemes.database.PoolProvider;

public class MySQLDatabase extends AbstractDatabase {
    public MySQLDatabase(PlayerThemes plugin, DatabaseSettings config) {
        super(plugin, config);
    }

    @Override
    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(config.jdbcUrl(plugin.getDataFolder()));
        hikariConfig.setUsername(config.mysqlUsername());
        hikariConfig.setPassword(config.mysqlPassword());
        hikariConfig.setPoolName("PlayerThemes-MySQL");

        PoolProvider.applySettings(hikariConfig, config.pool());

        this.dataSource = new HikariDataSource(hikariConfig);
        createTable();
    }

    @Override
    protected String saveQuery() {
        return """
            INSERT INTO playerthemes (uuid, theme) VALUES (?, ?)
            ON DUPLICATE KEY UPDATE theme = VALUES(theme);
            """;
    }
}
