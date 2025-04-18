package com.github.nicolasholanda.lettuce.poc.config;

/**
 * Provides the Redis connection URI.
 *
 * This abstraction allows switching between different Redis setups,
 * such as a local instance, a test container, or a production environment.
 */
public interface RedisConnectionProvider {

    /**
     * Returns the Redis URI to connect to.
     *
     * @return Redis URI in standard format (e.g., redis://localhost:6379)
     */
    String getRedisURI();
}