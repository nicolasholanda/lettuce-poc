package com.github.nicolasholanda.lettuce.poc.config;

/**
 * A RedisConnectionProvider implementation that connects to a local Redis instance.
 *
 * Useful for development environments where Redis is running locally.
 */
public class LocalRedisProvider implements RedisConnectionProvider {

    /**
     * Returns the URI for the local Redis server.
     *
     * @return Redis URI (e.g., redis://localhost:6379)
     */
    @Override
    public String getRedisURI() {
        return "redis://localhost:6379";
    }
}
