package com.server.demo.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.server.demo.memory.UploadControllerMemory.uploadIdChunkCountMap;
import static com.server.demo.memory.UploadControllerMemory.uploadIdHashValueMap;

@Service
public class UploadIdService {
    public String getRandomUploadId () {
        return UUID.randomUUID().toString();
    }
    public void putThatUploadIdIntoHashMaps(String uploadId) {
        uploadIdHashValueMap.put(uploadId, null);
        uploadIdChunkCountMap.put(uploadId, 0);
    }
}
