package com.server.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

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

        String currentChunkHash = getSha256Hash(chunk.getBytes());
        if (!currentChunkHash.equals(chunkHash))
            throw new Exception("Wrong hash");
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

            String newFullFileHash = getSha256Hash(Files.readAllBytes(videoFile.toPath()));

            if (!newFullFileHash.equals(uploadIdHashValueMap.get(uploadId))) {
                throw new Exception("File hash mismatch");
            }

            // Clean up maps
            uploadIdHashValueMap.remove(uploadId);
            uploadIdChunkCountMap.remove(uploadId);

            return ResponseEntity.ok("File uploaded successfully");
        }

        return ResponseEntity.ok("Chunk uploaded successfully");
    }
}
