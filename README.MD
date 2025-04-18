# Lettuce Redis Playground

This is a simple Java application that demonstrates basic usage of the [Lettuce](https://github.com/lettuce-io/lettuce-core) Redis client. The goal is to experiment with Lettuce features in a clean and minimal way — no heavy frameworks or extra dependencies.

## Features

- Connect to Redis using Lettuce
- Set and get keys
- Work with hashes
- Async operations
- Pub/Sub messaging

## Requirements

- Java 17+
- Maven
- Docker

## Running

Clone the project and run the Main class:
```bash
./mvnw compile exec:java
```

## What's Inside
The main entry point is `Main.java`. It calls methods in `RedisService` to demonstrate various Lettuce features.

Each feature is kept simple and self-contained, so you can experiment and modify things without much hassle.

## Why Lettuce?
Lettuce is a modern, thread-safe, non-blocking Redis client for Java. It supports both synchronous and asynchronous APIs, as well as reactive programming via Project Reactor.

If you're just getting started with Redis in Java or looking for a playground to try out different commands, this repo might be a good starting point.