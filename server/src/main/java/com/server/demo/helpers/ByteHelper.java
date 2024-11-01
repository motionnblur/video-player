package com.server.demo.helpers;

import java.util.Random;

public class ByteHelper {
    public static byte[] createRandomByteArray(int chunkSize) {
        byte[] newRandomByteArray = new byte[chunkSize];
        new Random().nextBytes(newRandomByteArray);

        return newRandomByteArray;
    }
}
