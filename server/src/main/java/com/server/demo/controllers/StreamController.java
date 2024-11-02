package com.server.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.RandomAccessFile;

@RestController
@RequestMapping("/api")
public class StreamController {
    @GetMapping("/videos/stream/{videoId}")
    public ResponseEntity<StreamingResponseBody> streamVideo(@PathVariable String videoId, HttpServletRequest request) {
        // Create a File reference to the video file
        File videoFile = new File("uploads/videos/" + videoId + ".mp4");
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        long fileLength = videoFile.length();
        long rangeStart;
        long rangeEnd = fileLength - 1;

        // Check for Range header in the request
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            rangeStart = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            }
        } else {
            rangeStart = 0;
        }

        long rangeLength = rangeEnd - rangeStart + 1;

        // Set the headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Length", String.valueOf(rangeLength));
        headers.add("Content-Type", "video/mp4"); // Change as needed

        // Create a StreamingResponseBody to write the specific range
        StreamingResponseBody responseBody = outputStream -> {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(videoFile, "r")) {
                randomAccessFile.seek(rangeStart); // Move to the start of the requested range
                byte[] buffer = new byte[1024];
                long bytesRead = 0;
                while (bytesRead < rangeLength) {
                    int bytesToRead = (int) Math.min(buffer.length, rangeLength - bytesRead);
                    int read = randomAccessFile.read(buffer, 0, bytesToRead);
                    if (read == -1) break; // End of file reached
                    outputStream.write(buffer, 0, read);
                    bytesRead += read;
                }
            }
        };

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(responseBody);
    }

}
