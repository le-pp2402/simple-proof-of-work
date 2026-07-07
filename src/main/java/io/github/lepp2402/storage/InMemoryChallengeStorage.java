package io.github.lepp2402.storage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

import io.github.lepp2402.core.ChallengeData;
import io.github.lepp2402.core.ChallengeStore;

import java.util.concurrent.TimeUnit;

public class InMemoryChallengeStorage implements ChallengeStore {
    private final Cache<String, ChallengeData> storage = Caffeine.newBuilder()
            .maximumSize(1_000_000)
            .expireAfter(new Expiry<String, ChallengeData>() {
                @Override
                public long expireAfterCreate(String key, ChallengeData value, long currentTime) {
                    long ttlMillis = value.expireAt() - System.currentTimeMillis();
                    return TimeUnit.MILLISECONDS.toNanos(Math.max(0, ttlMillis));
                }

                @Override
                public long expireAfterUpdate(String key, ChallengeData value, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(String key, ChallengeData value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    @Override
    public void save(String challengeId, int difficulty, long ttlSeconds) {
        storage.put(challengeId, new ChallengeData(difficulty, System.currentTimeMillis() + (ttlSeconds * 1000)));
    }

    @Override
    public ChallengeData getAndRemove(String challengeId) {
        ChallengeData data = storage.getIfPresent(challengeId);
        if (data != null) {
            storage.invalidate(challengeId);
        }
        return data;
    }
}
