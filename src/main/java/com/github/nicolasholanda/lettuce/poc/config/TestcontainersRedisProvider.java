package com.github.nicolasholanda.lettuce.poc.config;

import org.testcontainers.containers.GenericContainer;

public class TestcontainersRedisProvider implements RedisConnectionProvider {
    private final GenericContainer<?> redisContainer;

    public TestcontainersRedisProvider() {
        this.redisContainer = new GenericContainer<>("redis:7.2").withExposedPorts(6379);
        this.redisContainer.start();
    }

    @Override
    public String getRedisURI() {
        String host = redisContainer.getHost();
        int port = redisContainer.getMappedPort(6379);
        return "redis://" + host + ":" + port;
    }

    public void stop() {
        redisContainer.stop();
    }
}
