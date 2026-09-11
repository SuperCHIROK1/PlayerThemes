package io.inf8ty.playerthemes.database.redis;

import io.inf8ty.playerthemes.config.section.DatabaseSettings;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class RedisManager {
    private final DatabaseSettings.RedisSettings settings;

    private JedisPool pool;
    private JedisPubSub pubSub;

    public CompletableFuture<Void> connect(BiConsumer<UUID, String> onUpdate) {
        this.pool = settings.createPool();

        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                this.pubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        String[] args = message.split(";");
                        if (args.length < 3) return;

                        String server = args[0];
                        if (server.equals(settings.serverId())) return;

                        UUID uuid = UUID.fromString(args[1]);
                        String theme = args[2];

                        onUpdate.accept(uuid, theme);
                    }
                };
                jedis.subscribe(this.pubSub);
            } catch (Exception e) {
                throw new RuntimeException("Не удалось подключиться к Redis", e);
            }
        });
    }

    public CompletableFuture<Void> publish(UUID uuid, String theme) {
        if (pool == null || !pool.isClosed()) return CompletableFuture.completedFuture(null);

        String message = settings.serverId() + ";" + uuid + ";" + theme;

        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.publish(settings.serverId(), message);
            } catch (Exception e) {
                throw new RuntimeException("Не удалось отправить данные по Redis", e);
            }
        });
    }

    public void disconnect() {
        if (pubSub != null && pubSub.isSubscribed()) {
            pubSub.unsubscribe();
        }

        if (pool != null && !pool.isClosed()) {
            pool.close();
        }
    }
}
