package edu.icet.service.impl;

import edu.icet.dto.Donor;
import edu.icet.dto.DonorEntity;
import edu.icet.repository.DonorDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class DonorServiceImplTest {

    @MockBean
    private DonorDao donorDao;

    @MockBean
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private DonorServiceImpl donorService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Donor testDonor;
    private DonorEntity testDonorEntity;

    @BeforeEach
    void setUp() {
        testDonor = new Donor();
        testDonor.setId(1);
        testDonor.setEmailAddress("test@test.com");
        testDonor.setPassword("Test@123");
        testDonor.setContactNumber("1234567890");
        testDonor.setDistrict("TestDistrict");

        testDonorEntity = new DonorEntity();
        testDonorEntity.setId(1);
        testDonorEntity.setEmailAddress("test@test.com");
        testDonorEntity.setPassword("encodedPassword");
        testDonorEntity.setContactNumber("1234567890");
        testDonorEntity.setDistrict("TestDistrict");
    }

    @Test
    void shouldGetAllDonors() {
        when(donorDao.findAll()).thenReturn(Arrays.asList(testDonorEntity));
        List<Donor> result = donorService.getAll();
        assertThat(result).isNotEmpty();
        verify(donorDao).findAll();
    }

    @Test
    void shouldAddDonorWithProfileImage() {
        MockMultipartFile profileImage = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        when(fileStorageService.storeFile(any())).thenReturn("path/to/image");
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        donorService.addDonor(testDonor, profileImage);
        verify(donorDao).save(any(DonorEntity.class));
        verify(fileStorageService).storeFile(profileImage);
    }

    @Test
    void shouldValidatePassword() {
        String validPassword = "Test@123";
        String invalidPassword = "weak";
        assertTrue(DonorServiceImpl.isValidPassword(validPassword));
        assertFalse(DonorServiceImpl.isValidPassword(invalidPassword));
    }

    @Test
    void shouldDeleteDonorByContactNumber() {
        donorService.deleteDonorByContactNumber("1234567890");
        verify(donorDao).deleteByContactNumber("1234567890");
    }

    @Test
    void shouldSearchDonorById() {
        when(donorDao.findById(1)).thenReturn(Optional.of(testDonorEntity));
        Donor result = donorService.searchDonor(1);
        assertNotNull(result);
        verify(donorDao).findById(1);
    }

    @Test
    void shouldUpdateDonorById() {
        when(donorDao.existsById(1)).thenReturn(true);
        donorService.updateDonorById(testDonor);
        verify(donorDao).save(any(DonorEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentDonor() {
        when(donorDao.existsById(1)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> donorService.updateDonorById(testDonor));
    }

    @Test
    void shouldGetDonorByEmail() {
        when(donorDao.findByEmailAddress("test@test.com")).thenReturn(testDonorEntity);
        Donor result = donorService.getByEmail("test@test.com");
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmailAddress());
    }

    @Test
    void shouldCheckEmailExists() {
        when(donorDao.existsByEmailAddress("test@test.com")).thenReturn(true);
        boolean result = donorService.existsByEmailAddress("test@test.com");
        assertTrue(result);
    }

    @Test
    void shouldUpdatePassword() {
        when(donorDao.findByEmailAddress("test@test.com")).thenReturn(testDonorEntity);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        donorService.updatePassword("test@test.com", "newPassword");
        verify(donorDao).save(any(DonorEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordForNonExistentDonor() {
        when(donorDao.findByEmailAddress("nonexistent@test.com")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> donorService.updatePassword("nonexistent@test.com", "newPassword"));
    }

    @Test
    void shouldUpdateProfileImage() {
        when(donorDao.findById(1)).thenReturn(Optional.of(testDonorEntity));
        when(donorDao.save(any(DonorEntity.class))).thenReturn(testDonorEntity);
        Donor result = donorService.updateProfileImage(1, "new/image/path");
        assertNotNull(result);
        verify(donorDao).save(any(DonorEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProfileImageForNonExistentDonor() {
        when(donorDao.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> donorService.updateProfileImage(1, "new/image/path"));
    }

    @Test
    void shouldGetDonorsByDistrict() {
        when(donorDao.findByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(testDonorEntity));
        List<Donor> result = donorService.getDonorsByDistrict("TestDistrict");
        assertThat(result).isNotEmpty();
        verify(donorDao).findByDistrict("TestDistrict");
    }

    @Test
    void shouldVerifyPassword() {
        String inputPassword = "Test@123";
        String storedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        boolean result = donorService.verifyPassword(inputPassword, storedPassword);
        assertFalse(result);
    }

    @Test
    void shouldDeleteDonorById() {
        donorService.deleteDonorById(1);
        verify(donorDao).deleteById(1);
    }

    @Test
    void shouldGetDonorById() {
        when(donorDao.findById(1)).thenReturn(Optional.of(testDonorEntity));
        Donor result = donorService.getDonorById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }
}