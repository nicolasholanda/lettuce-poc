package com.github.nicolasholanda.lettuce.poc;

import com.github.nicolasholanda.lettuce.poc.config.LocalRedisProvider;
import com.github.nicolasholanda.lettuce.poc.config.RedisConnectionProvider;
import com.github.nicolasholanda.lettuce.poc.config.TestcontainersRedisProvider;
import com.github.nicolasholanda.lettuce.poc.service.RedisService;

public class Main {
    public static void main(String[] args) {
        boolean useTestcontainers = args.length < 1 || Boolean.parseBoolean(args[0]);

        RedisConnectionProvider provider = useTestcontainers
                ? new TestcontainersRedisProvider()
                : new LocalRedisProvider();

        RedisService redisService = new RedisService(provider);

        String key = "test";
        redisService.set(key, "123");

        System.out.println("Value: " + redisService.get(key));

        redisService.shutdown();

        if (provider instanceof TestcontainersRedisProvider tcp) {
            tcp.stop();
        }
    }
}
