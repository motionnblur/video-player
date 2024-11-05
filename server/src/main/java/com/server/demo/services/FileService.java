package com.server.demo.services;

import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class FileService {
    private final String uploadDir = "uploads/videos/";
    private final File directory;

    // Constructor injection for the File object, making it easier to mock
    public FileService(File directory) {
        this.directory = directory != null ? directory : new File(uploadDir);
    }

    // Default constructor for existing use cases
    public FileService() {
        this(new File("uploads/videos/"));
    }

    public void createDirectoryIfNotExist() {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.err.println("Failed to create directory: " + directory.getPath());
            }
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
