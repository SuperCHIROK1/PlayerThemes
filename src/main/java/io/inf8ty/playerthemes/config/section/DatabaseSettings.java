package io.inf8ty.playerthemes.config.section;

import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.*;

import java.io.File;

public record DatabaseSettings(
        DatabaseSettings.Type type,
        String sqlitePath,
        String mysqlIP,
        int mysqlPort,
        String mysqlUsername,
        String mysqlPassword,
        String mysqlDatabase,
        String mysqlArguments,
        ConfigurationSection pool,
        RedisSettings redis
) {
    public enum Type {
        SQLITE, MYSQL;

        public static DatabaseSettings.Type parse(String value) {
            if (value == null) return SQLITE;
            try {
                return DatabaseSettings.Type.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return SQLITE;
            }
        }
    }

    public String jdbcUrl(File dataFolder) {
        if (type == DatabaseSettings.Type.SQLITE) {
            return "jdbc:sqlite:" + new File(dataFolder, sqlitePath).getAbsolutePath();
        }
        return "jdbc:mysql://" + mysqlIP + ":" + mysqlPort + "/" + mysqlDatabase + mysqlArguments;
    }

    public record RedisSettings(
            String serverId,
            String channel,
            String host,
            int port,
            String username,
            String password,
            int database
    ) {
        public JedisPool createPool() {
            HostAndPort hostAndPort = new HostAndPort(host, port);

            JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                    .user(username == null || username.isEmpty() ? null : username)
                    .password(password == null || password.isEmpty() ? null : password)
                    .database(database)
                    .build();

            return new JedisPool(hostAndPort, clientConfig);
        }
    }
}
