package com.server.demo.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {
    private static final MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static String getSha256Hash(byte[] bytes) {
        byte[] hashBytes = md.digest(bytes);
        return bytesToHex(hashBytes);
    }
}
