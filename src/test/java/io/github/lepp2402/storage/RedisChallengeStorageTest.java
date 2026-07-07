package io.github.lepp2402.storage;

import io.github.lepp2402.core.ChallengeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisChallengeStorageTest {

    @Mock
    private RedisTemplate<String, ChallengeData> redisTemplate;

    @Mock
    private ValueOperations<String, ChallengeData> valueOperations;

    private RedisChallengeStorage storage;

    @BeforeEach
    void setUp() {
        storage = new RedisChallengeStorage(redisTemplate);
    }

    @Test
    void testSave() {
        String challengeId = "test-id-1";
        int difficulty = 5;
        long ttlSeconds = 60;
        String expectedKey = "pcapcha:test-id-1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        storage.save(challengeId, difficulty, ttlSeconds);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq(expectedKey), any(ChallengeData.class), eq(Duration.ofSeconds(ttlSeconds)));
    }

    @Test
    void testGetAndRemove() {
        String challengeId = "test-id-2";
        String expectedKey = "pcapcha:test-id-2";
        ChallengeData expectedData = new ChallengeData(4, 60);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getAndDelete(expectedKey)).thenReturn(expectedData);

        ChallengeData result = storage.getAndRemove(challengeId);

        assertNotNull(result);
        assertEquals(expectedData.difficulty(), result.difficulty());
        assertEquals(expectedData.expireAt(), result.expireAt());
        verify(valueOperations).getAndDelete(expectedKey);
    }

    @Test
    void testGetAndRemoveNotFound() {
        String challengeId = "test-id-3";
        String expectedKey = "pcapcha:test-id-3";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getAndDelete(expectedKey)).thenReturn(null);

        ChallengeData result = storage.getAndRemove(challengeId);

        assertNull(result);
    }
}
