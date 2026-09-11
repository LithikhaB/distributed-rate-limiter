package com.lithikha.rate_limiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS = 10;
    private static final Duration WINDOW = Duration.ofSeconds(60);

    // Lua script makes INCR + EXPIRE atomic — avoids race conditions
    // where two requests both read the count before either sets expiry
    private static final String SCRIPT =
        "local current = redis.call('INCR', KEYS[1]) " +
        "if tonumber(current) == 1 then " +
        "  redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
        "end " +
        "return current";

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String clientKey) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SCRIPT, Long.class);
        Long count = redisTemplate.execute(
            redisScript,
            Collections.singletonList("rate_limit:" + clientKey),
            String.valueOf(WINDOW.getSeconds())
        );
        return count != null && count <= MAX_REQUESTS;
    }
}