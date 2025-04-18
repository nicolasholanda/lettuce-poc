package com.github.nicolasholanda.lettuce.poc.config;

import org.testcontainers.containers.GenericContainer;

/**
 * A RedisConnectionProvider implementation that starts a Redis container using Testcontainers.
 *
 * Useful for integration tests or development environments where a real Redis instance is needed
 * but shouldn't be managed manually.
 */
public class TestcontainersRedisProvider implements RedisConnectionProvider {

    private final GenericContainer<?> redisContainer;

    /**
     * Starts a Redis container (version 7.2) using Testcontainers.
     * Exposes port 6379 for Redis access.
     */
    public TestcontainersRedisProvider() {
        this.redisContainer = new GenericContainer<>("redis:7.2").withExposedPorts(6379);
        this.redisContainer.start();
    }

    /**
     * Returns the URI for the running Redis container.
     *
     * @return Redis URI (e.g., redis://localhost:12345)
     */
    @Override
    public String getRedisURI() {
        String host = redisContainer.getHost();
        int port = redisContainer.getMappedPort(6379);
        return "redis://" + host + ":" + port;
    }

    /**
     * Stops the Redis container.
     */
    public void stop() {
        redisContainer.stop();
    }
}
