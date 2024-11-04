package com.server.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

import static com.server.demo.memory.UploadControllerMemory.uploadDir;

@SpringBootApplication
public class VideoServerApplication {

	public static void main(String[] args) {
		File directory = new File(uploadDir);
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				System.err.println("Failed to create directory: " + uploadDir);
			}
		}
		SpringApplication.run(VideoServerApplication.class, args);
	}

}
