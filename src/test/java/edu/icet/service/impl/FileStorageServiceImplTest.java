package edu.icet.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceImplTest {

    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        fileStorageService.init();
    }

    @Test
    void shouldInitializeUploadDirectory() {
        assertTrue(Files.exists(tempDir));
    }

    @Test
    void shouldStoreFile() {
        MultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String filePath = fileStorageService.storeFile(file);

        assertNotNull(filePath);
        assertTrue(filePath.startsWith("/campaigns/"));
        assertTrue(filePath.endsWith(".jpg"));

        LocalDate now = LocalDate.now();
        String yearMonth = String.format("%d/%02d", now.getYear(), now.getMonthValue());
        assertTrue(Files.exists(tempDir.resolve("campaigns").resolve(yearMonth).resolve(filePath.substring(filePath.lastIndexOf("/") + 1))));
    }

    @Test
    void shouldDeleteFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "test content".getBytes());

        fileStorageService.deleteFile("/test.txt");

        assertFalse(Files.exists(testFile));
    }

    @Test
    void shouldHandleDeleteNonExistentFile() {
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/nonexistent.txt"));
    }

    @Test
    void shouldStoreDonorProfileImage() {
        MultipartFile file = new MockMultipartFile(
                "profile.jpg",
                "profile.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String filePath = fileStorageService.storeDonorProfileImage(file);

        assertNotNull(filePath);
        assertTrue(filePath.startsWith("/donors/profiles/"));
        assertTrue(filePath.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve("donors").resolve("profiles").resolve(filePath.substring(filePath.lastIndexOf("/") + 1))));
    }

    @Test
    void shouldStoreHospitalProfileImage() {
        MultipartFile file = new MockMultipartFile(
                "profile.jpg",
                "profile.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String filePath = fileStorageService.storeHospitalProfileImage(file);

        assertNotNull(filePath);
        assertTrue(filePath.startsWith("/hospitals/profiles/"));
        assertTrue(filePath.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve("hospitals").resolve("profiles").resolve(filePath.substring(filePath.lastIndexOf("/") + 1))));
    }

    @Test
    void shouldThrowExceptionWhenStoringInvalidFile() {
        MultipartFile file = new MockMultipartFile(
                "test",
                "test",
                "text/plain",
                new byte[0]
        );

        assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(file));
    }

    @Test
    void shouldThrowExceptionWhenStoringDonorProfileWithInvalidFile() {
        MultipartFile file = new MockMultipartFile(
                "test",
                "test",
                "text/plain",
                new byte[0]
        );

        assertThrows(RuntimeException.class, () -> fileStorageService.storeDonorProfileImage(file));
    }

    @Test
    void shouldThrowExceptionWhenStoringHospitalProfileWithInvalidFile() {
        MultipartFile file = new MockMultipartFile(
                "test",
                "test",
                "text/plain",
                new byte[0]
        );

        assertThrows(RuntimeException.class, () -> fileStorageService.storeHospitalProfileImage(file));
    }

    @Test
    void shouldHandleIOExceptionDuringInitialization() {
        FileStorageServiceImpl service = new FileStorageServiceImpl();
        ReflectionTestUtils.setField(service, "uploadDir", "/invalid/path");

        assertThrows(RuntimeException.class, service::init);
    }
}