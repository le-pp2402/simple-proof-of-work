package io.github.lepp2402.storage;

import io.github.lepp2402.core.ChallengeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryChallengeStorageTest {

    private InMemoryChallengeStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryChallengeStorage();
    }

    @Test
    void testSaveAndGet() {
        String challengeId = "test-id-1";
        int difficulty = 4;
        long ttl = 10000; // 10 seconds

        storage.save(challengeId, difficulty, ttl);

        ChallengeData data = storage.getAndRemove(challengeId);

        assertNotNull(data);
        assertEquals(difficulty, data.difficulty());
        assertTrue(data.expireAt() > System.currentTimeMillis() - 1000);
    }

    @Test
    void testGetAndRemoveRemovesData() {
        String challengeId = "test-id-2";
        storage.save(challengeId, 4, 10000);

        ChallengeData data1 = storage.getAndRemove(challengeId);
        assertNotNull(data1);

        ChallengeData data2 = storage.getAndRemove(challengeId);
        assertNull(data2, "Should return null since data was removed");
    }

    @Test
    void testGetExpiredData() throws InterruptedException {
        String challengeId = "test-id-3";
        storage.save(challengeId, 4, 0); // 0 seconds

        Thread.sleep(100); // wait for Caffeine background cleanup or immediate expiration

        ChallengeData data = storage.getAndRemove(challengeId);
        assertNull(data, "Should return null for expired data");
    }
}
