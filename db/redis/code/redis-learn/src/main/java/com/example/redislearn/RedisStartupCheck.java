package com.example.redislearn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStartupCheck implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RedisStartupCheck.class);
    private final StringRedisTemplate redis;

    public RedisStartupCheck(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String pong = redis.getConnectionFactory().getConnection().ping();
            log.info("✅ Redis connection OK — PING returned: {}", pong);
        } catch (Exception e) {
            log.error("❌ Redis connection FAILED: {}", e.getMessage());
        }
    }
}