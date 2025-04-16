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

        redisService.shutdown();

        if (provider instanceof TestcontainersRedisProvider tcp) {
            tcp.stop();
        }
    }

    private static void simpleUsage() {
        String key = "test";
        redisService.set(key, "123");

        System.out.println("Value: " + redisService.get(key));
    }

    private static void ttlUsage() throws InterruptedException {
        redisService.setWithTTL("temp-key", "this is temporary", 6);
        System.out.println("TTL: " + redisService.getTTL("temp-key") + " seconds");

        Thread.sleep(2000);
        System.out.println("TTL after 2s: " + redisService.getTTL("temp-key"));
        System.out.println("Value with TTL: " + redisService.get("temp-key"));

        Thread.sleep(5000);
        System.out.println("Value after 7s: " + redisService.get("temp-key"));
    }

    private static void hashesUsage() {
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
                .thenAccept(result -> System.out.println("SET result: " + result));

        redisService.getAsync("async-key")
                .thenAccept(value -> System.out.println("GET value: " + value));
    }
}
