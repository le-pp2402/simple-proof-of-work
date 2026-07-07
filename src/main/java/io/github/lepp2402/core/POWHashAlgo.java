package io.github.lepp2402.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public enum POWHashAlgo {
    SHA256("SHA-256"), SHA512("SHA-512");

    private final String algorithm;

    POWHashAlgo(String algorithm) {
        this.algorithm = algorithm;
    }

    public String hash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hash = digest.digest(data.getBytes());
        return HexFormat.of().formatHex(hash);
    }
}
