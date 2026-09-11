package com.lithikha.rate_limiter;

import java.util.Collections;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimiterProperties properties;

    private static final String SCRIPT =
        "local current = redis.call('INCR', KEYS[1]) " +
        "if tonumber(current) == 1 then " +
        "  redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
        "end " +
        "return current";

    public RateLimiterService(StringRedisTemplate redisTemplate, RateLimiterProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public boolean isAllowed(String clientKey) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SCRIPT, Long.class);
        Long count = redisTemplate.execute(
            redisScript,
            Collections.singletonList("rate_limit:" + clientKey),
            String.valueOf(properties.getWindowSeconds())
        );
        return count != null && count <= properties.getMaxRequests();
    }
}