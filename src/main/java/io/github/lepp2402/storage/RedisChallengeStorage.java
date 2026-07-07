package io.github.lepp2402.storage;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;

import io.github.lepp2402.core.ChallengeData;
import io.github.lepp2402.core.ChallengeStore;

public class RedisChallengeStorage implements ChallengeStore {

    private final RedisTemplate<String, ChallengeData> redisTemplate;
    private static final String PREFIX = "pcapcha:";

    public RedisChallengeStorage(RedisTemplate<String, ChallengeData> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String challengeId, int difficulty, long ttlSeconds) {
        String key = PREFIX + challengeId;
        var ttl = Duration.ofSeconds(ttlSeconds);
        if (ttl != null)
            redisTemplate.opsForValue().set(key, new ChallengeData(difficulty, ttlSeconds), ttl);
    }

    @Override
    public ChallengeData getAndRemove(String challengeId) {
        String key = PREFIX + challengeId;
        ChallengeData data = redisTemplate.opsForValue().getAndDelete(key);
        if (data == null) {
            return null;
        }
        return data;
    }
}
