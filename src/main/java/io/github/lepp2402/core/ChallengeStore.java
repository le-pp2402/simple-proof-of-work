package io.github.lepp2402.core;

public interface ChallengeStore {
    void save(String challengeId, int difficulty, long ttl);

    ChallengeData getAndRemove(String challengeId);
}
