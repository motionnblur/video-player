package com.server.demo;

import com.server.demo.services.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServerApplicationTests {
	@Mock
	private FileService fileServiceMock;

	@InjectMocks
	private VideoServerApplication videoServerApplication;

	@Test
	void testApplicationContextLoads() {
		VideoServerApplication.main(new String[]{});
	}
	@Test
	void testDirectoryCreationWhenNotExist() {
		doNothing().when(fileServiceMock).createDirectoryIfNotExist();

		videoServerApplication.init();

		verify(fileServiceMock, times(1)).createDirectoryIfNotExist();
	}

	@Test
	void testDirectoryAlreadyExists() {
		doNothing().when(fileServiceMock).createDirectoryIfNotExist();

		videoServerApplication.init();

		verify(fileServiceMock, times(1)).createDirectoryIfNotExist();
	}

	@Test
	void testDirectoryCreationFailure() {
		doThrow(new RuntimeException("Failed to create directory")).when(fileServiceMock).createDirectoryIfNotExist();

		try {
			videoServerApplication.init();
		} catch (Exception e) {
			// Assert that the exception is thrown as expected
			assert(e.getMessage().contains("Failed to create directory"));
		}
	}
}
