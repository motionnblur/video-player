package com.server.demo.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteHelperTest {
    @Test
    public void testCreateRandomByteArray_PositiveChunkSize() {
        int chunkSize = 10;
        byte[] randomByteArray = ByteHelper.createRandomByteArray(chunkSize);
        assertNotNull(randomByteArray);
        assertEquals(chunkSize, randomByteArray.length);
    }

    @Test
    public void testCreateRandomByteArray_ZeroChunkSize() {
        int chunkSize = 0;
        byte[] randomByteArray = ByteHelper.createRandomByteArray(chunkSize);
        assertNotNull(randomByteArray);
        assertEquals(0, randomByteArray.length);
    }

    @Test
    public void testCreateRandomByteArray_NegativeChunkSize() {
        int chunkSize = -1;
        assertThrows(NegativeArraySizeException.class, () -> ByteHelper.createRandomByteArray(chunkSize));
    }

    @Test
    public void testCreateRandomByteArray_DifferentResults() {
        int chunkSize = 10;
        byte[] randomByteArray1 = ByteHelper.createRandomByteArray(chunkSize);
        byte[] randomByteArray2 = ByteHelper.createRandomByteArray(chunkSize);
        assertNotEquals(randomByteArray1, randomByteArray2);
    }

    @Test
    public void testCreateRandomByteArray_ContentIsRandom() {
        int chunkSize = 10;
        byte[] randomByteArray = ByteHelper.createRandomByteArray(chunkSize);
        boolean allZeros = true;
        for (byte b : randomByteArray) {
            if (b != 0) {
                allZeros = false;
                break;
            }
        }
        assertFalse(allZeros);
    }
}