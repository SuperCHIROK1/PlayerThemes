package io.inf8ty.playerthemes.database;

import com.zaxxer.hikari.HikariDataSource;
import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.config.section.DatabaseSettings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class AbstractDatabase {
    protected final PlayerThemes plugin;
    @SuppressWarnings("unused")
    protected final DatabaseSettings config;

    protected HikariDataSource dataSource;

    public abstract void connect();
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }

    protected abstract String saveQuery();

    public Connection connection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("HikariDataSource не инициализирован или закрыт.");
        }
        return dataSource.getConnection();
    }

    public void createTable() {
        try (Connection connection = connection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS playerthemes (
                            uuid VARCHAR(36) PRIMARY KEY,
                            theme VARCHAR(64) NOT NULL
                        );
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось создать таблицы в базе данных", e);
        }
    }

    public CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = connection();
                 PreparedStatement statement = connection.prepareStatement("SELECT uuid, theme FROM playerthemes;");
                 ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String theme = rs.getString("theme");
                    plugin.themeManager().players().put(uuid, theme);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при загрузке тем", e);
            }
        });
    }

    public CompletableFuture<Void> save(UUID uuid, String theme) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = connection();
                 PreparedStatement statement = connection.prepareStatement(saveQuery())) {

                statement.setString(1, uuid.toString());
                statement.setString(2, theme);

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка сохранения данных игрока", e);
            }
        });
    }

    public CompletableFuture<Void> saveAll() {
        return CompletableFuture.runAsync(() -> {
            Object2ObjectOpenHashMap<UUID, String> players = plugin.themeManager().players();
            if (players.isEmpty()) return;

            try (Connection connection = connection();
                 PreparedStatement statement = connection.prepareStatement(saveQuery())) {

                connection.setAutoCommit(false);

                for (Map.Entry<UUID, String> entry : players.entrySet()) {
                    statement.setString(1, entry.getKey().toString());
                    statement.setString(2, entry.getValue());
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка пакетного сохранения данных игроков", e);
            }
        });
    }

    public CompletableFuture<Void> remove(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = connection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM playerthemes WHERE uuid = ?;")) {

                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка удаления данных игрока" + uuid, e);
            }
        });
    }
}
