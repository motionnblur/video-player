package com.server.demo.services;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;

import static com.server.demo.helpers.HashHelper.getSha256Hash;
import static com.server.demo.memory.UploadControllerMemory.*;
import static com.server.demo.memory.UploadControllerMemory.uploadIdChunkCountMap;

@Service
public class UploadChunkService {
    @Autowired
    private FileService fileService;
    public void tryToSecurityCheckForParameters(MultipartFile chunk, int chunkNumber, int totalChunks, String uploadId, String chunkHash) throws Exception {
        if(!uploadIdHashValueMap.containsKey(uploadId)){
            throw new Exception("Invalid upload ID");
        }
        if(chunkNumber < 0 || chunkNumber >= totalChunks){
            throw new Exception("Invalid chunk number");
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(chunk.getBytes());String sha256hex = new String(Hex.encode(hash));

        if(!chunkHash.equals(sha256hex)){
            throw new Exception("Invalid hash value");
        }
    }
    public ResponseEntity<String> tryToUploadTheFileThenReturnResponse(MultipartFile chunk,
                                                                  int chunkNumber,
                                                                  int totalChunks,
                                                                  String fileName,
                                                                  String uploadId) throws Exception {
        // Save the chunk to a temporary file
        File tempFile = new File(fileService.getUploadDir() + fileName + ".part" + chunkNumber);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(chunk.getBytes());
        }

        // Update the count of received chunks
        int chunkCount = uploadIdChunkCountMap.getOrDefault(uploadId, 0) + 1;
        uploadIdChunkCountMap.put(uploadId, chunkCount);

        // If all chunks are received, concatenate them
        if (chunkCount == totalChunks) {
            File videoFile = new File(fileService.getUploadDir() + fileName);
            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                for (int i = 0; i < totalChunks; i++) {
                    File chunkFile = new File(fileService.getUploadDir() + fileName + ".part" + i);
                    fos.write(Files.readAllBytes(chunkFile.toPath()));
                    chunkFile.delete(); // Delete the chunk after appending
                }
            }

            // Clean up maps
            uploadIdHashValueMap.remove(uploadId);
            uploadIdChunkCountMap.remove(uploadId);

            return ResponseEntity.ok("File uploaded successfully");
        }

        return ResponseEntity.ok("Chunk uploaded successfully");
    }
}
