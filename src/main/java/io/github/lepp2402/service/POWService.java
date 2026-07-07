package io.github.lepp2402.service;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import io.github.lepp2402.core.ChallengeData;
import io.github.lepp2402.core.ChallengeStore;
import io.github.lepp2402.core.POWHashAlgo;
import io.github.lepp2402.core.PowChallenge;

public class POWService {
    private final ChallengeStore store;

    public POWService(ChallengeStore store) {
        this.store = store;
    }

    public PowChallenge genChallenge(int difficult, long ttlSecond) {
        String challengeId = UUID.randomUUID().toString();
        store.save(challengeId, difficult, ttlSecond);
        return new PowChallenge(challengeId, difficult, ttlSecond);
    }

    public boolean verifyWork(String challengeId, String nonce, String algo) throws NoSuchAlgorithmException {
        ChallengeData data = store.getAndRemove(challengeId);

        if (data == null) {
            return false;
        }

        String dataToHash = challengeId + nonce;

        POWHashAlgo hashAlgo = POWHashAlgo.valueOf(algo);
        String hash = hashAlgo.hash(dataToHash);

        return hash.startsWith("0".repeat(data.difficulty()));
    }
}
