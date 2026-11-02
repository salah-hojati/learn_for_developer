package com.example.redislearn;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final StringRedisTemplate redis;

    public RedisController(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** Health check against Redis */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        String pong = redis.getConnectionFactory().getConnection().ping();
        return Map.of("ping", pong == null ? "null" : pong);
    }

    /** SET with optional TTL (in seconds) */
    @PostMapping("/set")
    public Map<String, Object> set(@RequestParam String key,
                                   @RequestParam String value,
                                   @RequestParam(required = false) Long ttlSeconds) {
        if (ttlSeconds != null) {
            redis.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        } else {
            redis.opsForValue().set(key, value);
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("key", key);
        resp.put("value", value);
        resp.put("ttlSeconds", ttlSeconds == null ? "none" : ttlSeconds);
        return resp;
    }

    /** GET value + remaining TTL */
    @GetMapping("/get")
    public Map<String, Object> get(@RequestParam String key) {
        String value = redis.opsForValue().get(key);
        Long ttl = redis.getExpire(key);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("key", key);
        resp.put("value", value == null ? "null" : value);
        resp.put("ttlSeconds", ttl);   // -1 = no expiry, -2 = key doesn't exist
        return resp;
    }

    /** INCR — a classic Redis counter (great for learning atomicity) */
    @PostMapping("/incr")
    public Map<String, Object> incr(@RequestParam String key) {
        Long v = redis.opsForValue().increment(key);
        return Map.of("key", key, "value", v);
    }

    /** DELETE */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String key) {
        Boolean deleted = redis.delete(key);
        return Map.of("key", key, "deleted", Boolean.TRUE.equals(deleted));
    }
}