package com.server.demo.controllers;

import com.server.demo.entities.VideoEntity;
import com.server.demo.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private VideoRepository videoRepository;

    private final String uploadDir = "uploads/videos/";

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                              @RequestParam("chunkNumber") int chunkNumber,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @RequestParam("fileName") String fileName) {
        try {
            // Create upload directory if it doesn't exist
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the chunk to a temporary file
            File tempFile = new File(uploadDir + fileName + ".part" + chunkNumber);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(chunk.getBytes());
            }

            // Check if we have all the chunks
            boolean allChunksPresent = true;
            for (int i = 0; i < totalChunks; i++) {
                File chunkFile = new File(uploadDir + fileName + ".part" + i);
                if (!chunkFile.exists()) {
                    allChunksPresent = false;
                    break;
                }
            }

            // If all chunks are present, concatenate them
            if (allChunksPresent) {
                File videoFile = new File(uploadDir + fileName);
                try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                    for (int i = 0; i < totalChunks; i++) {
                        File chunkFile = new File(uploadDir + fileName + ".part" + i);
                        fos.write(Files.readAllBytes(chunkFile.toPath()));
                        chunkFile.delete(); // Delete the chunk after appending
                    }
                }

                // Save video metadata to database
                //VideoEntity video = new VideoEntity();
                //video.setTitle(fileName);
                //video.setFilePath(uploadDir + fileName);
                //videoRepository.save(video);
            }

            return ResponseEntity.ok("Chunk uploaded successfully");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading chunk");
        }
    }
}
