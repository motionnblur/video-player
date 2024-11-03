package com.server.demo.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.server.demo.memory.UploadControllerMemory.uploadIdChunkCountMap;
import static com.server.demo.memory.UploadControllerMemory.uploadIdHashValueMap;

@Service
public class HashValueService {
    public void mergeThatHashWithUploadId(String hashValue, String uploadId) throws Exception {
        if (uploadIdHashValueMap.containsKey(uploadId) && !uploadIdHashValueMap.isEmpty()) {
            uploadIdHashValueMap.put(uploadId, hashValue);
        }else{
            throw new Exception("Invalid upload ID");
        }
    }
}
