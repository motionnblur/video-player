package com.server.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {
    private FileService fileService;

    @Mock
    private File mockedDirectoryFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the mocked File object into FileService
        fileService = new FileService(mockedDirectoryFile);
    }

    @Test
    void should_Not_Create_Directory_If_It_Already_Exist() {
        // Arrange: Mock the behavior of the File class
        when(mockedDirectoryFile.exists()).thenReturn(true);

        // Act: Call the method
        fileService.createDirectoryIfNotExist();

        // Assert: Ensure mkdirs() is not called when the directory exists
        verify(mockedDirectoryFile, never()).mkdirs();
    }

    @Test
    void should_Create_Directory_If_It_Does_Not_Exist() {
        // Arrange: Mock the behavior of the File class
        when(mockedDirectoryFile.exists()).thenReturn(false);
        when(mockedDirectoryFile.mkdirs()).thenReturn(true);

        // Act: Call the method
        fileService.createDirectoryIfNotExist();

        // Assert: Verify mkdirs was called since the directory doesn't exist
        verify(mockedDirectoryFile).mkdirs();
    }

    @Test
    void testCreateDirectoryIfNotExist_whenDirectoryCreationFails() {
        // Arrange: Mock the behavior of the File class
        when(mockedDirectoryFile.exists()).thenReturn(false);
        when(mockedDirectoryFile.mkdirs()).thenReturn(false); // Simulate failure to create the directory

        // Act: Call the method
        fileService.createDirectoryIfNotExist();

        // Assert: You can either verify a log message here or ensure no exceptions are thrown.
        // Since this is a void method, we just verify that it did not crash:
        verify(mockedDirectoryFile, times(1)).mkdirs();
    }

    @Test
    void testGetUploadDir() {
        // Act: Call the method
        String result = fileService.getUploadDir();

        // Assert: Ensure the result is the correct directory path
        assertEquals("uploads/videos/", result, "Upload directory should be 'uploads/videos/'");
    }
}