package com.server.demo.controllers;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.server.demo.services.HashValueService;
import com.server.demo.services.UploadChunkService;
import com.server.demo.services.UploadIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.server.demo.helpers.HashHelper.*;
import static com.server.demo.memory.UploadControllerMemory.*;

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

    @PostMapping("/hashValue")
    public ResponseEntity<String> getHashValue(@RequestParam("fileHash") String fileHash,
                                               @RequestParam("uploadId") String uploadId) {
        try {
            hashValueService.mergeThatHashWithUploadId(fileHash, uploadId);

            return ResponseEntity.ok("Hash value updated successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
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
            uploadChunkService.securityCheckForParameters(chunk, chunkNumber, totalChunks, fileName, uploadId, chunkHash);
            uploadChunkService.createUploadDirectoryIfNotExists();

            return uploadChunkService.uploadTheFile(fileName, chunk, chunkNumber, totalChunks, uploadId);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
