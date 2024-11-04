package com.server.demo.services;

import com.server.demo.memory.UploadControllerMemory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class UploadChunkServiceTest {
    @Test
    void tryToSecurityCheckForParameters() throws Exception {
        UploadControllerMemory uMemory = Mockito.mock(UploadControllerMemory.class);
        UploadChunkService uService = Mockito.mock(UploadChunkService.class);

        HashMap<String, String> uploadIdHashValueMap = new HashMap<>();
        uploadIdHashValueMap.put("someUploadId", "someHashValue");

        Mockito.when(uMemory.getUploadIdHashValueMap()).thenReturn(uploadIdHashValueMap);

        uService.tryToSecurityCheckForParameters(Mockito.mock(MultipartFile.class),
                1,
                2,
                "someUploadId",
                "someHashValue");

        verify(uService).tryToSecurityCheckForParameters(Mockito.any(MultipartFile.class),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.eq("someUploadId"),
                Mockito.eq("someHashValue"));
    }
}