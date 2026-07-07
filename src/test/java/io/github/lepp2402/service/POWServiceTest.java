package io.github.lepp2402.service;

import io.github.lepp2402.core.ChallengeData;
import io.github.lepp2402.core.ChallengeStore;
import io.github.lepp2402.core.POWHashAlgo;
import io.github.lepp2402.core.PowChallenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class POWServiceTest {

    @Mock
    private ChallengeStore store;

    private POWService service;

    @BeforeEach
    void setUp() {
        service = new POWService(store);
    }

    @Test
    void testGenChallenge() {
        int difficulty = 3;
        long ttlSecond = 120;

        PowChallenge challenge = service.genChallenge(difficulty, ttlSecond);

        assertNotNull(challenge);
        assertNotNull(challenge.challengeId());
        assertEquals(difficulty, challenge.difficult());
        assertEquals(ttlSecond, challenge.expiresIn());

        verify(store).save(eq(challenge.challengeId()), eq(difficulty), eq(ttlSecond));
    }

    @Test
    void testVerifyWorkSuccess() throws NoSuchAlgorithmException {
        // Prepare valid data
        String challengeId = "my-challenge";
        int difficulty = 2; // requiring two leading zeros
        String algo = "SHA256";

        ChallengeData mockData = new ChallengeData(difficulty, System.currentTimeMillis() + 10000);
        when(store.getAndRemove(challengeId)).thenReturn(mockData);

        // Find a valid nonce manually
        String validNonce = findNonce(challengeId, difficulty, algo);

        boolean result = service.verifyWork(challengeId, validNonce, algo);

        assertTrue(result, "Verify work should return true for valid nonce");
    }

    @Test
    void testVerifyWorkFailureInvalidNonce() throws NoSuchAlgorithmException {
        String challengeId = "my-challenge-2";
        int difficulty = 4;
        String algo = "SHA256";

        ChallengeData mockData = new ChallengeData(difficulty, System.currentTimeMillis() + 10000);
        when(store.getAndRemove(challengeId)).thenReturn(mockData);

        boolean result = service.verifyWork(challengeId, "invalid-nonce-123", algo);

        assertFalse(result, "Verify work should return false for invalid nonce");
    }

    @Test
    void testVerifyWorkFailureChallengeNotFound() throws NoSuchAlgorithmException {
        String challengeId = "missing-challenge";
        String algo = "SHA256";

        when(store.getAndRemove(challengeId)).thenReturn(null);

        boolean result = service.verifyWork(challengeId, "any-nonce", algo);

        assertFalse(result, "Verify work should return false if challenge is not found");
    }

    // Helper method to find a valid nonce for testing
    private String findNonce(String challengeId, int difficulty, String algoName) throws NoSuchAlgorithmException {
        POWHashAlgo algo = POWHashAlgo.valueOf(algoName);
        long nonce = 0;
        String prefix = "0".repeat(difficulty);
        while (true) {
            String hash = algo.hash(challengeId + nonce);
            if (hash.startsWith(prefix)) {
                return String.valueOf(nonce);
            }
            nonce++;
        }
    }
}
