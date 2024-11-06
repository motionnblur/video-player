package com.server.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;import org.mockito.junit.jupiter.MockitoExtension;

import static com.server.demo.memory.UploadControllerMemory.uploadIdHashValueMap;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HashValueServiceTest {
    @InjectMocks
    private HashValueService hashValueService;

    @BeforeEach
    void setUp() {
        uploadIdHashValueMap.clear();
    }

    @Test
    void testMergeThatHashWithUploadId() {
        String uploadId = "uploadId1";
        String hashValue = "hashValue1";
        hashValueService.mergeThatHashWithUploadId(hashValue, uploadId);
        assertEquals(hashValue, uploadIdHashValueMap.get(uploadId));
    }

    @Test
    void testTryToCheckIfHashWeJustGotInParamAlreadyExists_HashExists() {
        String uploadId = "uploadId2";
        String fileHash = "fileHash2";
        uploadIdHashValueMap.put(uploadId, fileHash);
        assertDoesNotThrow(() -> hashValueService.tryToCheckIfHashWeJustGotInParamAlreadyExists(uploadId, fileHash));
    }

    @Test
    void testTryToCheckIfHashWeJustGotInParamAlreadyExists_HashDoesNotExist() {
        String uploadId = "uploadId3";
        String fileHash = "fileHash3";
        Exception exception = assertThrows(Exception.class, () -> {
            hashValueService.tryToCheckIfHashWeJustGotInParamAlreadyExists(uploadId, fileHash);
        });
        assertEquals("Invalid hash value", exception.getMessage());
    }
}