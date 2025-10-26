package com.bookingservice.booking.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDebugService {

    private final RedissonClient redissonClient;

    @Scheduled(fixedRate = 30000)
    public void logKeys() {
        try {
            List<String> keys = redissonClient.getKeys().getKeysStream().toList();

            if (keys.isEmpty()) {
                log.info("В Redis нет ключей.");
            } else {
                log.info("Ключи в Redis ({}): {}", keys.size(), keys);
                for (String key : keys) {
                    long ttl = redissonClient.getKeys().remainTimeToLive(key);
                    log.info("ключ {} (TTL: {} ms)", key, ttl);
                }
            }

        } catch (Exception e) {
            log.error("Ошибка при попытке получить ключи Redis: {}", e.getMessage());
        }
    }
}