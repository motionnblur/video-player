package com.server.demo.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.server.demo.memory.UploadControllerMemory.uploadIdChunkCountMap;
import static com.server.demo.memory.UploadControllerMemory.uploadIdHashValueMap;

@Service
public class HashValueService {
    public void mergeThatHashWithUploadId(String hashValue, String uploadId){
        uploadIdHashValueMap.put(uploadId, hashValue);
    }

    public void tryToCheckIfHashWeJustGotInParamAlreadyExists(String uploadId, String fileHash) throws Exception {
        if (!uploadIdHashValueMap.containsKey(uploadId)) {
            throw new Exception("Invalid hash value");
        }
    }
}
