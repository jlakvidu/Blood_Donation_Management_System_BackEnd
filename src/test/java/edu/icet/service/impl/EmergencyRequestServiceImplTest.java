package edu.icet.service.impl;

import edu.icet.dto.EmergencyRequest;
import edu.icet.dto.EmergencyResponseDTO;
import edu.icet.entity.EmergencyRequestEntity;
import edu.icet.repository.EmergencyRequestDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmergencyRequestServiceImplTest {

    @MockBean
    private EmergencyRequestDao emergencyRequestRepository;

    @MockBean
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private EmergencyRequestServiceImpl emergencyRequestService;

    private EmergencyRequest testRequest;
    private EmergencyRequestEntity testEntity;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        testRequest = new EmergencyRequest();
        testRequest.setPatientName("Test Patient");
        testRequest.setBloodType("A+");
        testRequest.setHospital("Test Hospital");
        testRequest.setDistrict("Test District");
        testRequest.setHospitalEmail("test@hospital.com");
        testRequest.setContactNumber("1234567890");
        testRequest.setUnitsNeeded(2);
        testRequest.setUrgencyLevel("HIGH");
        testRequest.setDescription("Test Description");

        testEntity = new EmergencyRequestEntity();
        testEntity.setId(1L);
        testEntity.setPatientName("Test Patient");
        testEntity.setBloodType("A+");
        testEntity.setHospital("Test Hospital");
        testEntity.setDistrict("Test District");
        testEntity.setHospitalEmail("test@hospital.com");
        testEntity.setContactNumber("1234567890");
        testEntity.setUnitsNeeded(2);
        testEntity.setUrgencyLevel("HIGH");
        testEntity.setDescription("Test Description");
        testEntity.setCreatedAt(testDateTime);
        testEntity.setStatus("ACTIVE");
    }

    @Test
    void shouldCreateRequest() {
        when(emergencyRequestRepository.save(any(EmergencyRequestEntity.class))).thenReturn(testEntity);

        EmergencyResponseDTO result = emergencyRequestService.createRequest(testRequest);

        assertNotNull(result);
        assertEquals("Test Patient", result.getPatientName());
        verify(emergencyRequestRepository).save(any(EmergencyRequestEntity.class));
    }

    @Test
    void shouldGetAllActiveRequests() {
        when(emergencyRequestRepository.findByStatusOrderByCreatedAtDesc("ACTIVE"))
                .thenReturn(Arrays.asList(testEntity));

        List<EmergencyResponseDTO> result = emergencyRequestService.getAllActiveRequests();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Patient", result.get(0).getPatientName());
    }

    @Test
    void shouldGetRequestById() {
        when(emergencyRequestRepository.findById(1L)).thenReturn(Optional.of(testEntity));

        EmergencyResponseDTO result = emergencyRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals("Test Patient", result.getPatientName());
    }

    @Test
    void shouldThrowExceptionWhenRequestNotFound() {
        when(emergencyRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> emergencyRequestService.getRequestById(1L));
    }

    @Test
    void shouldCloseRequest() {
        when(emergencyRequestRepository.findById(1L)).thenReturn(Optional.of(testEntity));

        emergencyRequestService.closeRequest(1L);

        verify(emergencyRequestRepository).save(any(EmergencyRequestEntity.class));
        assertEquals("CLOSED", testEntity.getStatus());
    }

    @Test
    void shouldDeleteRequest() {
        when(emergencyRequestRepository.findById(1L)).thenReturn(Optional.of(testEntity));

        emergencyRequestService.deleteRequest(1L);

        verify(emergencyRequestRepository).delete(testEntity);
    }

    @Test
    void shouldUpdateEmergencyRequest() {
        when(emergencyRequestRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(emergencyRequestRepository.save(any(EmergencyRequestEntity.class))).thenReturn(testEntity);

        EmergencyRequestEntity result = emergencyRequestService.updateEmergencyRequest(1L, testRequest);

        assertNotNull(result);
        assertEquals("Test Patient", result.getPatientName());
        verify(emergencyRequestRepository).save(any(EmergencyRequestEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentRequest() {
        when(emergencyRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> emergencyRequestService.updateEmergencyRequest(1L, testRequest));
    }

    @Test
    void shouldGetEmergenciesBetweenDates() {
        LocalDateTime startDate = testDateTime.minusDays(1);
        LocalDateTime endDate = testDateTime.plusDays(1);

        when(emergencyRequestRepository.findByCreatedAtBetween(startDate, endDate))
                .thenReturn(Arrays.asList(testEntity));

        List<EmergencyRequestEntity> result = emergencyRequestService.getEmergenciesBetweenDates(startDate, endDate);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(emergencyRequestRepository).findByCreatedAtBetween(startDate, endDate);
    }
}