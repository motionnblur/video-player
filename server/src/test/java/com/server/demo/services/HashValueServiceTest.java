package com.server.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashValueServiceTest {
    @Test
    void mergeThatHashWithUploadId() {
        HashValueService hService = Mockito.mock(HashValueService.class);
        hService.mergeThatHashWithUploadId("someUploadId", "someHashValue");
        verify(hService).mergeThatHashWithUploadId("someUploadId", "someHashValue");
    }

    @Test
    void tryToCheckIfHashWeJustGotInParamAlreadyExists() throws Exception {
        HashValueService hService = Mockito.mock(HashValueService.class);
        assertDoesNotThrow(() -> hService.tryToCheckIfHashWeJustGotInParamAlreadyExists("someUploadId", "someHashValue"));
        verify(hService).tryToCheckIfHashWeJustGotInParamAlreadyExists("someUploadId", "someHashValue");
    }
}