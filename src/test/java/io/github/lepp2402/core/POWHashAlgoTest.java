package io.github.lepp2402.core;

import org.junit.jupiter.api.Test;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;

class POWHashAlgoTest {

    @Test
    void testSha256Hash() throws NoSuchAlgorithmException {
        String data = "test-data";
        String hash = POWHashAlgo.SHA256.hash(data);
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 hex string is 64 characters
    }

    @Test
    void testSha512Hash() throws NoSuchAlgorithmException {
        String data = "test-data";
        String hash = POWHashAlgo.SHA512.hash(data);
        assertNotNull(hash);
        assertEquals(128, hash.length()); // SHA-512 hex string is 128 characters
    }
}
