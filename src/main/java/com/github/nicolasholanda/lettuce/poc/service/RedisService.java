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

public class RedisService {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisAsyncCommands<String, String> asyncCommands;
    private final RedisCommands<String, String> syncCommands;
    private final RedisReactiveCommands<String, String> reactiveCommands;

    public RedisService(RedisConnectionProvider provider) {
        this.redisClient = RedisClient.create(provider.getRedisURI());
        this.connection = redisClient.connect();
        this.asyncCommands = connection.async();
        this.syncCommands = connection.sync();
        this.reactiveCommands = connection.reactive();
    }

    public void set(String key, String value) {
        syncCommands.set(key, value);
    }

    public String get(String key) {
        return syncCommands.get(key);
    }

    public void setWithTTL(String key, String value, long seconds) {
        syncCommands.setex(key, seconds, value);
    }

    public Long getTTL(String key) {
        return syncCommands.ttl(key);
    }

    public void setUserProfile(String userId, Map<String, String> fields) {
        syncCommands.hset("user:" + userId, fields);
    }

    public Map<String, String> getUserProfile(String userId) {
        return syncCommands.hgetall("user:" + userId);
    }

    public CompletableFuture<String> setAsync(String key, String value) {
        return asyncCommands.set(key, value).toCompletableFuture();
    }

    public CompletableFuture<String> getAsync(String key) {
        return asyncCommands.get(key).toCompletableFuture();
    }

    public Mono<String> setReactive(String key, String value) {
        return reactiveCommands.set(key, value);
    }

    public Mono<String> getReactive(String key) {
        return reactiveCommands.get(key);
    }

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

    public void publish(String channel, String message) {
        connection.sync().publish(channel, message);
    }

    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }
}
