package com.github.nicolasholanda.lettuce.poc;

import com.github.nicolasholanda.lettuce.poc.config.LocalRedisProvider;
import com.github.nicolasholanda.lettuce.poc.config.RedisConnectionProvider;
import com.github.nicolasholanda.lettuce.poc.config.TestcontainersRedisProvider;
import com.github.nicolasholanda.lettuce.poc.service.RedisService;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        boolean useTestcontainers = args.length < 1 || Boolean.parseBoolean(args[0]);

        RedisConnectionProvider provider = useTestcontainers
                ? new TestcontainersRedisProvider()
                : new LocalRedisProvider();

        RedisService redisService = new RedisService(provider);

        String key = "test";
        redisService.set(key, "123");

        System.out.println("Value: " + redisService.get(key));

        redisService.setWithTTL("temp-key", "this is temporary", 6);
        System.out.println("TTL: " + redisService.getTTL("temp-key") + " seconds");

        Thread.sleep(2000);
        System.out.println("TTL after 2s: " + redisService.getTTL("temp-key"));
        System.out.println("Value with TTL: " + redisService.get("temp-key"));

        Thread.sleep(5000);
        System.out.println("Value after 7s: " + redisService.get("temp-key"));

        redisService.shutdown();

        if (provider instanceof TestcontainersRedisProvider tcp) {
            tcp.stop();
        }
    }
}
