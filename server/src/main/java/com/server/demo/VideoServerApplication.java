package com.server.demo;

import com.server.demo.services.FileService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoServerApplication {
	private final FileService fileService;

	// Constructor injection of FileService
	@Autowired
	public VideoServerApplication(FileService fileService) {
		this.fileService = fileService;
	}

	public static void main(String[] args) {
		SpringApplication.run(VideoServerApplication.class, args);
	}
	@PostConstruct
	public void init() {
		fileService.createDirectoryIfNotExist();
	}
}
