package com.github.nicolasholanda.lettuce.poc;

import com.github.nicolasholanda.lettuce.poc.config.LocalRedisProvider;
import com.github.nicolasholanda.lettuce.poc.config.RedisConnectionProvider;
import com.github.nicolasholanda.lettuce.poc.config.TestcontainersRedisProvider;
import com.github.nicolasholanda.lettuce.poc.service.RedisService;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class to demonstrate basic Lettuce Redis features:
 * synchronous operations, TTL, hashes, async, reactive, and pub/sub.
 *
 * <p>You can choose between using a local Redis instance or a Redis container via Testcontainers.
 * By default (no arguments or true as first argument), it will start a Redis container.
 */
public class Main {

    private static RedisService redisService;

    public static void main(String[] args) throws InterruptedException {
        boolean useTestcontainers = args.length < 1 || Boolean.parseBoolean(args[0]);

        RedisConnectionProvider provider = useTestcontainers
                ? new TestcontainersRedisProvider()
                : new LocalRedisProvider();

        redisService = new RedisService(provider);

        simpleUsage();
        ttlUsage();
        hashesUsage();
        asyncUsage();
        reactiveUsage();
        pubSubUsage();

        redisService.shutdown();

        if (provider instanceof TestcontainersRedisProvider tcp) {
            tcp.stop();
        }
    }

    /**
     * Demonstrates basic set/get using synchronous API.
     */
    private static void simpleUsage() {
        System.out.println("-------------- SIMPLE USAGE --------------");

        String key = "test";
        redisService.set(key, "123");

        System.out.println("Value: " + redisService.get(key));
    }

    /**
     * Demonstrates setting a value with TTL and observing its expiration.
     */
    private static void ttlUsage() throws InterruptedException {
        System.out.println("-------------- TTL USAGE --------------");

        redisService.setWithTTL("temp-key", "this is temporary", 6);
        System.out.println("TTL: " + redisService.getTTL("temp-key") + " seconds");

        Thread.sleep(2000);
        System.out.println("TTL after 2s: " + redisService.getTTL("temp-key"));
        System.out.println("Value with TTL: " + redisService.get("temp-key"));

        Thread.sleep(5000);
        System.out.println("Value after 7s: " + redisService.get("temp-key"));
    }

    /**
     * Demonstrates storing and retrieving a Redis hash (user profile).
     */
    private static void hashesUsage() {
        System.out.println("-------------- HASHES USAGE --------------");

        Map<String, String> user = new HashMap<>();
        user.put("name", "Alice");
        user.put("email", "alice@example.com");
        user.put("role", "admin");

        redisService.setUserProfile("123", user);

        System.out.println("User profile:");
        System.out.println(redisService.getUserProfile("123"));
    }

    /**
     * Demonstrates asynchronous set/get using CompletableFuture.
     */
    private static void asyncUsage() {
        redisService.setAsync("async-key", "yo from the future")
                .thenCompose(setResult -> {
                    System.out.println("-------------- ASYNC USAGE --------------");
                    System.out.println("SET result: " + setResult);
                    return redisService.getAsync("async-key");
                })
                .thenAccept(getResult -> {
                    System.out.println("GET value: " + getResult);
                });
    }

    /**
     * Demonstrates reactive set/get using Reactor Mono.
     */
    private static void reactiveUsage() {
        redisService.setReactive("reactive-key", "hi from reactive")
                .doOnNext(result -> System.out.println("-------------- REACTIVE USAGE --------------"))
                .doOnNext(result -> System.out.println("SET result: " + result))
                .then(redisService.getReactive("reactive-key"))
                .doOnNext(value -> System.out.println("GET value: " + value))
                .block(); // Just to trigger execution here
    }

    /**
     * Demonstrates simple publish/subscribe messaging using Redis Pub/Sub.
     */
    private static void pubSubUsage() throws InterruptedException {
        redisService.subscribe("chat");

        Thread.sleep(1000); // Give time for subscription

        redisService.publish("chat", "Hey from Lettuce Pub/Sub!");
        redisService.publish("chat", "Another message");

        Thread.sleep(2000); // Wait to see the messages
    }
}