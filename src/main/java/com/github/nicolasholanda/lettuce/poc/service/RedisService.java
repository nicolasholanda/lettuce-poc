package com.github.nicolasholanda.lettuce.poc.service;

import com.github.nicolasholanda.lettuce.poc.config.RedisConnectionProvider;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisService {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;

    public RedisService(RedisConnectionProvider provider) {
        this.redisClient = RedisClient.create(provider.getRedisURI());
        this.connection = redisClient.connect();
    }

    public void set(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        commands.set(key, value);
    }

    public String get(String key) {
        RedisCommands<String, String> commands = connection.sync();
        return commands.get(key);
    }

    public void setWithTTL(String key, String value, long seconds) {
        RedisCommands<String, String> commands = connection.sync();
        commands.setex(key, seconds, value);
    }

    public Long getTTL(String key) {
        RedisCommands<String, String> commands = connection.sync();
        return commands.ttl(key);
    }

    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }
}
