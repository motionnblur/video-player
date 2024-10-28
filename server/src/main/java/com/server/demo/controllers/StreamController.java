package com.server.demo.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api")
public class StreamController {
    @GetMapping("/videos/stream/{videoId}")
    public ResponseEntity<byte[]> streamVideo(@PathVariable String videoId) {
        try {
            File videoFile = new File("uploads/videos/" + videoId + ".webm");
            if (!videoFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] videoBytes = Files.readAllBytes(videoFile.toPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/webm")
                    .body(videoBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
