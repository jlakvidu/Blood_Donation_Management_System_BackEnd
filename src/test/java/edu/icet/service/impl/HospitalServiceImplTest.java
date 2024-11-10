package edu.icet.service.impl;

import edu.icet.dto.Hospital;
import edu.icet.entity.HospitalEntity;
import edu.icet.repository.HospitalDao;
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
class HospitalServiceImplTest {

    @MockBean
    private HospitalDao hospitalDao;

    @MockBean
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private HospitalServiceImpl hospitalService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Hospital testHospital;
    private HospitalEntity testHospitalEntity;

    @BeforeEach
    void setUp() {
        testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("Test Hospital");
        testHospital.setEmailAddress("test@hospital.com");
        testHospital.setPassword("Test@123");
        testHospital.setDistrict("TestDistrict");

        testHospitalEntity = new HospitalEntity();
        testHospitalEntity.setId(1L);
        testHospitalEntity.setName("Test Hospital");
        testHospitalEntity.setEmailAddress("test@hospital.com");
        testHospitalEntity.setPassword("encodedPassword");
        testHospitalEntity.setDistrict("TestDistrict");
    }

    @Test
    void shouldGetAllHospitals() {
        when(hospitalDao.findAll()).thenReturn(Arrays.asList(testHospitalEntity));
        List<Hospital> result = hospitalService.getAll();
        assertThat(result).isNotEmpty();
        verify(hospitalDao).findAll();
    }

    @Test
    void shouldAddHospitalWithProfileImage() {
        MockMultipartFile profileImage = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        when(fileStorageService.storeFile(any())).thenReturn("path/to/image");
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        hospitalService.addHospital(testHospital, profileImage);
        verify(hospitalDao).save(any(HospitalEntity.class));
        verify(fileStorageService).storeFile(profileImage);
    }

    @Test
    void shouldValidatePassword() {
        String validPassword = "Test@123";
        String invalidPassword = "weak";
        assertTrue(HospitalServiceImpl.isValidPassword(validPassword));
        assertFalse(HospitalServiceImpl.isValidPassword(invalidPassword));
    }

    @Test
    void shouldSearchHospitalById() {
        when(hospitalDao.findById(1L)).thenReturn(Optional.of(testHospitalEntity));
        Hospital result = hospitalService.searchHospitalById(1L);
        assertNotNull(result);
        verify(hospitalDao).findById(1L);
    }

    @Test
    void shouldSearchHospitalByName() {
        when(hospitalDao.findByName("Test Hospital")).thenReturn(testHospitalEntity);
        Hospital result = hospitalService.searchHospitalByName("Test Hospital");
        assertNotNull(result);
        assertEquals("Test Hospital", result.getName());
    }

    @Test
    void shouldVerifyPassword() {
        String inputPassword = "Test@123";
        String storedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        boolean result = hospitalService.verifyPassword(inputPassword, storedPassword);
        assertFalse(result);
    }

    @Test
    void shouldGetHospitalsByDistrict() {
        when(hospitalDao.findByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(testHospitalEntity));
        List<Hospital> result = hospitalService.getHospitalByDistrict("TestDistrict");
        assertThat(result).isNotEmpty();
        verify(hospitalDao).findByDistrict("TestDistrict");
    }

    @Test
    void shouldDeleteHospitalById() {
        hospitalService.deleteHospitalById(1L);
        verify(hospitalDao).deleteById(1L);
    }

    @Test
    void shouldUpdateHospital() {
        hospitalService.updateHospital(testHospital);
        verify(hospitalDao).save(any(HospitalEntity.class));
    }

    @Test
    void shouldGetHospitalByEmail() {
        when(hospitalDao.findByEmailAddress("test@hospital.com")).thenReturn(testHospitalEntity);
        Hospital result = hospitalService.getByEmail("test@hospital.com");
        assertNotNull(result);
        assertEquals("test@hospital.com", result.getEmailAddress());
    }

    @Test
    void shouldCheckHospitalExists() {
        when(hospitalDao.existsByEmailAddress("test@hospital.com")).thenReturn(true);
        boolean result = hospitalService.hospitalExists("test@hospital.com");
        assertTrue(result);
    }

    @Test
    void shouldUpdatePassword() {
        when(hospitalDao.findByEmailAddress("test@hospital.com")).thenReturn(testHospitalEntity);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        hospitalService.updatePassword("test@hospital.com", "newPassword");
        verify(hospitalDao).save(any(HospitalEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordForNonExistentHospital() {
        when(hospitalDao.findByEmailAddress("nonexistent@hospital.com")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> hospitalService.updatePassword("nonexistent@hospital.com", "newPassword"));
    }

    @Test
    void shouldUpdateProfileImage() {
        when(hospitalDao.findById(1L)).thenReturn(Optional.of(testHospitalEntity));
        when(hospitalDao.save(any(HospitalEntity.class))).thenReturn(testHospitalEntity);
        Hospital result = hospitalService.updateProfileImage(1L, "new/image/path");
        assertNotNull(result);
        verify(hospitalDao).save(any(HospitalEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProfileImageForNonExistentHospital() {
        when(hospitalDao.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> hospitalService.updateProfileImage(1L, "new/image/path"));
    }
}