package com.server.demo.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UploadControllerMemoryTest {
    private UploadControllerMemory uploadControllerMemory;

    @BeforeEach
    void setUp() {
        // Initialize the class before each test
        uploadControllerMemory = new UploadControllerMemory();

        // Clear the maps before each test to ensure a clean state
        UploadControllerMemory.uploadIdHashValueMap.clear();
        UploadControllerMemory.uploadIdChunkCountMap.clear();
    }

    @Test
    void testGetUploadIdHashValueMap_whenEmpty() {
        // Test if the map is empty initially
        assertTrue(uploadControllerMemory.getUploadIdHashValueMap().isEmpty(), "Map should be empty initially.");
    }

    @Test
    void testGetUploadIdChunkCountMap_whenEmpty() {
        // Test if the chunk count map is empty initially
        assertTrue(uploadControllerMemory.getUploadIdChunkCountMap().isEmpty(), "Chunk count map should be empty initially.");
    }

    @Test
    void testUploadIdHashValueMap_whenValuesAdded() {
        // Add some values to the map
        UploadControllerMemory.uploadIdHashValueMap.put("upload1", "hash1");
        UploadControllerMemory.uploadIdHashValueMap.put("upload2", "hash2");

        // Test if the map has the correct size
        assertEquals(2, uploadControllerMemory.getUploadIdHashValueMap().size(), "Map size should be 2.");

        // Test the content of the map
        assertEquals("hash1", uploadControllerMemory.getUploadIdHashValueMap().get("upload1"), "Hash for upload1 should be 'hash1'");
        assertEquals("hash2", uploadControllerMemory.getUploadIdHashValueMap().get("upload2"), "Hash for upload2 should be 'hash2'");
    }

    @Test
    void testUploadIdChunkCountMap_whenValuesAdded() {
        // Add some values to the chunk count map
        UploadControllerMemory.uploadIdChunkCountMap.put("upload1", 5);
        UploadControllerMemory.uploadIdChunkCountMap.put("upload2", 10);

        // Test if the map has the correct size
        assertEquals(2, uploadControllerMemory.getUploadIdChunkCountMap().size(), "Chunk count map size should be 2.");

        // Test the content of the map
        assertEquals(5, uploadControllerMemory.getUploadIdChunkCountMap().get("upload1"), "Chunk count for upload1 should be 5");
        assertEquals(10, uploadControllerMemory.getUploadIdChunkCountMap().get("upload2"), "Chunk count for upload2 should be 10");
    }

    @Test
    void testClearUploadIdHashValueMap() {
        // Add some values to the map
        UploadControllerMemory.uploadIdHashValueMap.put("upload1", "hash1");
        UploadControllerMemory.uploadIdHashValueMap.put("upload2", "hash2");

        // Clear the map
        UploadControllerMemory.uploadIdHashValueMap.clear();

        // Test if the map is empty after clearing
        assertTrue(uploadControllerMemory.getUploadIdHashValueMap().isEmpty(), "Map should be empty after clearing.");
    }

    @Test
    void testClearUploadIdChunkCountMap() {
        // Add some values to the chunk count map
        UploadControllerMemory.uploadIdChunkCountMap.put("upload1", 5);
        UploadControllerMemory.uploadIdChunkCountMap.put("upload2", 10);

        // Clear the map
        UploadControllerMemory.uploadIdChunkCountMap.clear();

        // Test if the map is empty after clearing
        assertTrue(uploadControllerMemory.getUploadIdChunkCountMap().isEmpty(), "Chunk count map should be empty after clearing.");
    }
}