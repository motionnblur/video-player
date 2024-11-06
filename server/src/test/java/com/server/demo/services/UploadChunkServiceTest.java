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
//    @Test
//    void tryToSecurityCheckForParameters() throws Exception {
//        UploadControllerMemory uMemory = Mockito.mock(UploadControllerMemory.class);
//        UploadChunkService uService = Mockito.mock(UploadChunkService.class);
//
//        HashMap<String, String> uploadIdHashValueMap = new HashMap<>();
//        uploadIdHashValueMap.put("someUploadId", "someHashValue");
//
//        Mockito.when(uMemory.getUploadIdHashValueMap()).thenReturn(uploadIdHashValueMap);
//
//        uService.tryToSecurityCheckForParameters(
//
//                1,
//                2,
//                "someUploadId");
//
//        verify(uService).tryToSecurityCheckForParameters(
//                Mockito.anyInt(),
//                Mockito.anyInt(),
//                Mockito.eq("someUploadId")
//                );
//    }
}