package com.github.nicolasholanda.lettuce.poc.service;

import com.github.nicolasholanda.lettuce.poc.config.RedisConnectionProvider;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A simple wrapper around Lettuce Redis client to demonstrate core Redis operations.
 * Supports sync, async, reactive, and pub/sub usage.
 */
public class RedisService {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisAsyncCommands<String, String> asyncCommands;
    private final RedisCommands<String, String> syncCommands;
    private final RedisReactiveCommands<String, String> reactiveCommands;

    /**
     * Initializes Redis connections based on a connection provider.
     *
     * @param provider the Redis connection provider
     */
    public RedisService(RedisConnectionProvider provider) {
        this.redisClient = RedisClient.create(provider.getRedisURI());
        this.connection = redisClient.connect();
        this.asyncCommands = connection.async();
        this.syncCommands = connection.sync();
        this.reactiveCommands = connection.reactive();
    }

    /**
     * Sets a key with a value using synchronous API.
     */
    public void set(String key, String value) {
        syncCommands.set(key, value);
    }

    /**
     * Gets the value of a key using synchronous API.
     */
    public String get(String key) {
        return syncCommands.get(key);
    }

    /**
     * Sets a key with a value and expiration time (in seconds).
     */
    public void setWithTTL(String key, String value, long seconds) {
        syncCommands.setex(key, seconds, value);
    }

    /**
     * Gets the time-to-live of a key (in seconds).
     */
    public Long getTTL(String key) {
        return syncCommands.ttl(key);
    }

    /**
     * Sets a user profile as a Redis hash.
     *
     * @param userId user identifier
     * @param fields map of field names and values
     */
    public void setUserProfile(String userId, Map<String, String> fields) {
        syncCommands.hset("user:" + userId, fields);
    }

    /**
     * Gets a user profile hash.
     *
     * @param userId user identifier
     * @return map of field names and values
     */
    public Map<String, String> getUserProfile(String userId) {
        return syncCommands.hgetall("user:" + userId);
    }

    /**
     * Sets a key asynchronously.
     */
    public CompletableFuture<String> setAsync(String key, String value) {
        return asyncCommands.set(key, value).toCompletableFuture();
    }

    /**
     * Gets a key asynchronously.
     */
    public CompletableFuture<String> getAsync(String key) {
        return asyncCommands.get(key).toCompletableFuture();
    }

    /**
     * Sets a key using reactive API.
     */
    public Mono<String> setReactive(String key, String value) {
        return reactiveCommands.set(key, value);
    }

    /**
     * Gets a key using reactive API.
     */
    public Mono<String> getReactive(String key) {
        return reactiveCommands.get(key);
    }

    /**
     * Subscribes to a Redis channel and prints received messages.
     */
    public void subscribe(String channel) {
        StatefulRedisPubSubConnection<String, String> pubSubConnection = redisClient.connectPubSub();
        pubSubConnection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                System.out.println("[SUB] Received message: " + message + " on channel: " + channel);
            }

            @Override public void message(String pattern, String channel, String message) {}
            @Override public void subscribed(String channel, long count) {
                System.out.println("[SUB] Subscribed on channel: " + channel);
            }
            @Override public void psubscribed(String pattern, long count) {}
            @Override public void unsubscribed(String channel, long count) {}
            @Override public void punsubscribed(String pattern, long count) {}
        });

        pubSubConnection.sync().subscribe(channel);
    }

    /**
     * Publishes a message to a Redis channel.
     */
    public void publish(String channel, String message) {
        connection.sync().publish(channel, message);
    }

    /**
     * Checks if the given key exists in Redis.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    public boolean exists(String key) {
        return syncCommands.exists(key) > 0;
    }

    /**
     * Async check for key existence.
     *
     * @param key the key to check
     * @return CompletableFuture with true if key exists, false otherwise
     */
    public CompletableFuture<Boolean> existsAsync(String key) {
        return asyncCommands.exists(key)
                .toCompletableFuture()
                .thenApply(count -> count > 0);
    }

    /**
     * Closes Redis connection and shuts down the client.
     */
    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }
}
