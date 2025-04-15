package com.github.nicolasholanda.lettuce.poc.config;

public class LocalRedisProvider implements RedisConnectionProvider {
    @Override
    public String getRedisURI() {
        return "redis://localhost:6379";
    }
}
