package com.lithikha.rate_limiter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class RateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimiterApplication.class, args);
    }

    @Bean
    CommandLineRunner testRedisConnection(StringRedisTemplate redisTemplate) {
        return args -> {
            redisTemplate.opsForValue().set("wiring-test", "connected");
            String result = redisTemplate.opsForValue().get("wiring-test");
            System.out.println(">>> Redis wiring check: " + result);
        };
    }
}