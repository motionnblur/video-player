package com.server.demo.controllers;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.server.demo.Helpers.HashHelper.bytesToHex;

@RestController
@RequestMapping("/api")
public class UploadController {
    private final Map<String, String> uploadIdHashValueMap = new HashMap<>();
    private final Map<String, Integer> uploadIdChunkCountMap = new HashMap<>();

    private final String uploadDir = "uploads/videos/";

    @GetMapping("/upload-id")
    public ResponseEntity<String> getUploadId() {
        String uploadId = UUID.randomUUID().toString();
        uploadIdHashValueMap.put(uploadId, null);
        uploadIdChunkCountMap.put(uploadId, 0);
        return ResponseEntity.ok(uploadId);
    }

    @PostMapping("/hashValue")
    public ResponseEntity<String> getHashValue(@RequestParam("fileHash") String fileHash,
                                               @RequestParam("uploadId") String uploadId) {
        if (uploadIdHashValueMap.containsKey(uploadId)) {
            uploadIdHashValueMap.put(uploadId, fileHash);
            return ResponseEntity.ok("Hash value updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid upload ID");
        }
    }

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                              @RequestParam("chunkNumber") int chunkNumber,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @RequestParam("fileName") String fileName,
                                              @RequestParam("uploadId") String uploadId,
                                              @RequestParam("chunkHash") String chunkHash) {
        try {
            if (uploadId == null || !uploadIdHashValueMap.containsKey(uploadId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing upload ID");
            }
            if(chunkNumber < 0 || chunkNumber >= totalChunks){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid chunk number");
            }
            if(chunkHash == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chunk hash is missing");
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(chunk.getBytes());
            String currentChunkHash = bytesToHex(hashBytes);

            if (!currentChunkHash.equals(chunkHash)) {
                // If the hashes do not match, request the client to re-upload the chunk
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chunk hash mismatch. Please re-upload chunk " + chunkNumber);
            }

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the chunk to a temporary file
            File tempFile = new File(uploadDir + fileName + ".part" + chunkNumber);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(chunk.getBytes());
            }

            // Update the count of received chunks
            int chunkCount = uploadIdChunkCountMap.getOrDefault(uploadId, 0) + 1;
            uploadIdChunkCountMap.put(uploadId, chunkCount);

            // If all chunks are received, concatenate them
            if (chunkCount == totalChunks) {
                File videoFile = new File(uploadDir + fileName);
                try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                    for (int i = 0; i < totalChunks; i++) {
                        File chunkFile = new File(uploadDir + fileName + ".part" + i);
                        fos.write(Files.readAllBytes(chunkFile.toPath()));
                        chunkFile.delete(); // Delete the chunk after appending
                    }
                }

                // Verify the file hash
                ByteSource byteSource2 = com.google.common.io.Files.asByteSource(videoFile);
                HashCode hc2 = byteSource2.hash(Hashing.sha256());
                String checksum2 = hc2.toString();
                if (!checksum2.equals(uploadIdHashValueMap.get(uploadId))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File hash mismatch");
                }

                // Clean up maps
                uploadIdHashValueMap.remove(uploadId);
                uploadIdChunkCountMap.remove(uploadId);

                return ResponseEntity.ok("File uploaded successfully");
            }

            return ResponseEntity.ok("Chunk uploaded successfully");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
