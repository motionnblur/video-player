package com.server.demo.controllers;

import com.server.demo.services.HashValueService;
import com.server.demo.services.UploadChunkService;
import com.server.demo.services.UploadIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {
    @Autowired
    private UploadIdService uploadIdService;
    @Autowired
    private HashValueService hashValueService;
    @Autowired
    private UploadChunkService uploadChunkService;

    @GetMapping("/upload-id")
    public ResponseEntity<String> getUploadId() {
        String uploadId = uploadIdService.getRandomUploadId();
        uploadIdService.putThatUploadIdIntoHashMaps(uploadId);

        return ResponseEntity.ok(uploadId);
    }

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                              @RequestParam("chunkNumber") int chunkNumber,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @RequestParam("fileName") String fileName,
                                              @RequestParam("uploadId") String uploadId,
                                              @RequestParam("chunkHash") String chunkHash) {
        try {
            uploadChunkService.tryToSecurityCheckForParameters(chunk, chunkNumber, totalChunks, uploadId, chunkHash);
            return uploadChunkService.tryToUploadTheFileThenReturnResponse(chunk, chunkNumber, totalChunks, fileName, uploadId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
