package com.server.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamControllerTest {
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private StreamController streamController;

    @Test
    public void testStreamVideo_Found() throws Exception {
        // Arrange
        String videoId = "test-video";
        File videoDir = new File("uploads/videos");
        videoDir.mkdirs();

        File videoFile = new File("uploads/videos/" + videoId + ".mp4");
        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "rw")) {
            raf.setLength(5 * 1024 * 1024); // 5MB
            raf.seek(0);
            byte[] buffer = new byte[1024];
            for (int i = 0; i < 5 * 1024; i++) {
                raf.write(buffer); // write random data
            }
        }
        assertEquals(5 * 1024 * 1024, videoFile.length());
        // Act
        ResponseEntity<StreamingResponseBody> response = streamController.streamVideo(videoId, request);

        // Assert
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals("video/mp4", response.getHeaders().getContentType().toString());

        videoFile.delete();
        videoDir.delete();
    }

    @Test
    public void testStreamVideo_NotFound() throws Exception {
        // Arrange
        String videoId = "non-existent-video";

        // Act
        ResponseEntity<StreamingResponseBody> response = streamController.streamVideo(videoId, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testStreamVideo_RangeHeader() throws Exception {
        // Arrange
        String videoId = "test-video";
        File videoDir = new File("uploads/videos");
        videoDir.mkdirs();

        File videoFile = new File("uploads/videos/" + videoId + ".mp4");
        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "rw")) {
            raf.setLength(5 * 1024 * 1024); // 5MB
            raf.seek(0);
            byte[] buffer = new byte[1024];
            for (int i = 0; i < 5 * 1024; i++) {
                raf.write(buffer); // write random data
            }
        }
        assertEquals(5 * 1024 * 1024, videoFile.length());

        String rangeHeader = "bytes=10-20";

        when(request.getHeader("Range")).thenReturn(rangeHeader);

        // Act
        ResponseEntity<StreamingResponseBody> response = streamController.streamVideo(videoId, request);

        // Assert
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals("[bytes 10-20/5242880]", response.getHeaders().get("Content-Range").toString());

        videoFile.delete();
        videoDir.delete();
    }
}