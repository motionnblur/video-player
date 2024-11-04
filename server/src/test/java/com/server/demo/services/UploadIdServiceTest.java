package com.server.demo.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class UploadIdServiceTest {

    @Test
    void getRandomUploadId() {
        UploadIdService uService = Mockito.mock(UploadIdService.class);
        uService.getRandomUploadId();
        verify(uService).getRandomUploadId();
    }

    @Test
    void putThatUploadIdIntoHashMaps() {
        UploadIdService uService = Mockito.mock(UploadIdService.class);
        uService.putThatUploadIdIntoHashMaps("someUploadId");
        verify(uService).putThatUploadIdIntoHashMaps("someUploadId");
    }
}