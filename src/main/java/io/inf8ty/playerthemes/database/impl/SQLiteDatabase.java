package io.inf8ty.playerthemes.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.DatabaseSettings;
import io.inf8ty.playerthemes.database.AbstractDatabase;
import io.inf8ty.playerthemes.database.PoolProvider;

public class SQLiteDatabase extends AbstractDatabase {
    public SQLiteDatabase(PlayerThemes plugin, DatabaseSettings config) {
        super(plugin, config);
    }

    @Override
    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl(config.jdbcUrl(plugin.getDataFolder()));
        hikariConfig.setPoolName("PlayerThemes-SQLite");

        PoolProvider.applySettings(hikariConfig, config.pool());

        this.dataSource = new HikariDataSource(hikariConfig);
        createTable();
    }

    @Override
    protected String saveQuery() {
        return """
            INSERT INTO playerthemes (uuid, theme) VALUES (?, ?)
            ON CONFLICT(uuid) DO UPDATE SET theme = excluded.theme;
            """;
    }
}
