package com.server.demo.controllers;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.server.demo.entities.VideoEntity;
import com.server.demo.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {
    private final Map<String, String> uploadIdHashValueMap = new HashMap<>();
    private final Map<String, Integer> uploadIdChunkCountMap = new HashMap<>();

    @Autowired
    private VideoRepository videoRepository;

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
                                              @RequestParam("uploadId") String uploadId) {
        try {
            if (uploadId == null || !uploadIdHashValueMap.containsKey(uploadId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing upload ID");
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
                ByteSource byteSource = com.google.common.io.Files.asByteSource(videoFile);
                HashCode hc = byteSource.hash(Hashing.sha256());
                String checksum = hc.toString();
                if (!checksum.equals(uploadIdHashValueMap.get(uploadId))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File hash mismatch");
                }

                // Save video metadata to database
                VideoEntity video = new VideoEntity();
                video.setTitle(fileName);
                video.setFilePath(uploadDir + fileName);
                videoRepository.save(video);

                // Clean up maps
                uploadIdHashValueMap.remove(uploadId);
                uploadIdChunkCountMap.remove(uploadId);

                return ResponseEntity.ok("File uploaded successfully");
            }

            return ResponseEntity.ok("Chunk uploaded successfully");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading chunk");
        }
    }
}
