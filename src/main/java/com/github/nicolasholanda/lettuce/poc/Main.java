package com.github.nicolasholanda.lettuce.poc;

import com.github.nicolasholanda.lettuce.poc.config.LocalRedisProvider;
import com.github.nicolasholanda.lettuce.poc.config.RedisConnectionProvider;
import com.github.nicolasholanda.lettuce.poc.config.TestcontainersRedisProvider;
import com.github.nicolasholanda.lettuce.poc.service.RedisService;

import java.util.HashMap;
import java.util.Map;

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

    private static void simpleUsage() {
        System.out.println("-------------- SIMPLE USAGE --------------");

        String key = "test";
        redisService.set(key, "123");

        System.out.println("Value: " + redisService.get(key));
    }

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

    private static void reactiveUsage() {
        redisService.setReactive("reactive-key", "hi from reactive")
                .doOnNext(result -> System.out.println("-------------- REACTIVE USAGE --------------"))
                .doOnNext(result -> System.out.println("SET result: " + result))
                .then(redisService.getReactive("reactive-key"))
                .doOnNext(value -> System.out.println("GET value: " + value))
                .block(); // Just to trigger execution here
    }

    private static void pubSubUsage() throws InterruptedException {
        redisService.subscribe("chat");

        Thread.sleep(1000);

        redisService.publish("chat", "Hey from Lettuce Pub/Sub!");
        redisService.publish("chat", "Another message");

        Thread.sleep(2000);
    }
}
