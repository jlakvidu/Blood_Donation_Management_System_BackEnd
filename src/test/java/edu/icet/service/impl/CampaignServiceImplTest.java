package edu.icet.service.impl;

import edu.icet.dto.CampaignRequest;
import edu.icet.dto.CampaignResponse;
import edu.icet.entity.CampaignEntity;
import edu.icet.repository.CampaignDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CampaignServiceImplTest {

    @MockBean
    private CampaignDao campaignRepository;

    @MockBean
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private CampaignServiceImpl campaignService;

    private CampaignEntity testCampaign;
    private CampaignRequest testRequest;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        testCampaign = new CampaignEntity();
        testCampaign.setId(1L);
        testCampaign.setTitle("Test Campaign");
        testCampaign.setDate(LocalDate.from(testDateTime));
        testCampaign.setVenue("Test Venue");
        testCampaign.setDistrict("Test District");
        testCampaign.setDescription("Test Description");
        testCampaign.setHospitalName("Test Hospital");
        testCampaign.setHospitalEmail("test@hospital.com");
        testCampaign.setContactNumber("1234567890");
        testCampaign.setCreatedAt(testDateTime);

        testRequest = new CampaignRequest();
        testRequest.setTitle("Test Campaign");
        testRequest.setDate(LocalDate.from(testDateTime));
        testRequest.setVenue("Test Venue");
        testRequest.setDistrict("Test District");
        testRequest.setDescription("Test Description");
        testRequest.setHospitalName("Test Hospital");
        testRequest.setHospitalEmail("test@hospital.com");
        testRequest.setContactNumber("1234567890");
    }

    @Test
    void shouldGetAllCampaigns() {
        when(campaignRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(testCampaign));

        List<CampaignResponse> result = campaignService.getAllCampaigns();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Campaign", result.get(0).getTitle());
        verify(campaignRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldCreateCampaignWithImage() {
        MultipartFile image = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        testRequest.setImage(image);

        when(fileStorageService.storeFile(any())).thenReturn("path/to/image");
        when(campaignRepository.save(any(CampaignEntity.class))).thenReturn(testCampaign);

        CampaignResponse result = campaignService.createCampaign(testRequest);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        verify(fileStorageService).storeFile(image);
        verify(campaignRepository).save(any(CampaignEntity.class));
    }

    @Test
    void shouldCreateCampaignWithoutImage() {
        when(campaignRepository.save(any(CampaignEntity.class))).thenReturn(testCampaign);

        CampaignResponse result = campaignService.createCampaign(testRequest);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        verify(campaignRepository).save(any(CampaignEntity.class));
        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    void shouldDeleteCampaign() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        campaignService.deleteCampaignById(1L);

        verify(campaignRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCampaign() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> campaignService.deleteCampaignById(1L));
    }

    @Test
    void shouldGetCampaignById() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        CampaignEntity result = campaignService.getCampaignById(1L);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
    }

    @Test
    void shouldReturnNullForNonExistentCampaignId() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        CampaignEntity result = campaignService.getCampaignById(1L);

        assertNull(result);
    }

    @Test
    void shouldUpdateCampaign() {
        when(campaignRepository.save(testCampaign)).thenReturn(testCampaign);

        CampaignEntity result = campaignService.updateCampaign(testCampaign);

        assertNotNull(result);
        assertEquals("Test Campaign", result.getTitle());
        verify(campaignRepository).save(testCampaign);
    }

    @Test
    void shouldGetCampaignsBetweenDates() {
        LocalDateTime startDate = testDateTime.minusDays(1);
        LocalDateTime endDate = testDateTime.plusDays(1);

        when(campaignRepository.findByCreatedAtBetween(startDate, endDate))
                .thenReturn(Arrays.asList(testCampaign));

        List<CampaignEntity> result = campaignService.getCampaignsBetweenDates(startDate, endDate);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(campaignRepository).findByCreatedAtBetween(startDate, endDate);
    }
}