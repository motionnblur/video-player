package com.server.demo.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashHelperTest {
    @Test
    public void testGetSha256Hash() throws Exception {
        byte[] input = "Hello, World!".getBytes();
        String expectedHash = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
        String actualHash = HashHelper.getSha256Hash(input);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testGetSha256Hash_NullInput() {
        assertThrows(NullPointerException.class, () -> HashHelper.getSha256Hash(null));
    }

    @Test
    public void testGetSha256Hash_EmptyInput() throws Exception {
        byte[] input = new byte[0];
        String actualHash = HashHelper.getSha256Hash(input);
        assertNotNull(actualHash);
    }

    @Test
    public void testBytesToHex() {
        byte[] input = {0x12, 0x34, 0x56, 0x78};
        String expectedHex = "12345678";
        String actualHex = HashHelper.bytesToHex(input);
        assertEquals(expectedHex, actualHex);
    }

    @Test
    public void testBytesToHex_NullInput() {
        assertThrows(NullPointerException.class, () -> HashHelper.bytesToHex(null));
    }

    @Test
    public void testBytesToHex_EmptyInput() {
        byte[] input = new byte[0];
        String actualHex = HashHelper.bytesToHex(input);
        assertEquals("", actualHex);
    }
}