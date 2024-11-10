package edu.icet.service.impl;

import edu.icet.dto.Donor;
import edu.icet.dto.Hospital;
import edu.icet.entity.AppointmentEntity;
import edu.icet.entity.CampaignEntity;
import edu.icet.entity.EmergencyRequestEntity;
import edu.icet.entity.HospitalEntity;
import edu.icet.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReportServiceImplTest {

    @MockBean
    private DonorService donorService;

    @MockBean
    private HospitalService hospitalService;

    @MockBean
    private CampaignService campaignService;

    @MockBean
    private EmergencyRequestService emergencyRequestService;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ReportServiceImpl reportService;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now().minusMonths(1);
        endDate = LocalDateTime.now();

        Donor donor1 = new Donor();
        donor1.setAge(20);
        donor1.setBloodType("A+");

        Donor donor2 = new Donor();
        donor2.setAge(30);
        donor2.setBloodType("B+");

        Hospital hospital1 = new Hospital();
        hospital1.setType("Government");
        hospital1.setBloodBankCapacity("75");

        Hospital hospital2 = new Hospital();
        hospital2.setType("Private");
        hospital2.setBloodBankCapacity("100");

        when(donorService.getDonorsByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(donor1, donor2));

        when(hospitalService.getHospitalByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(hospital1, hospital2));
    }

    @Test
    void shouldGenerateDonorAnalytics() {
        Map<String, Object> analytics = reportService.generateDonorAnalyticsByDistrict("TestDistrict");

        assertNotNull(analytics);
        assertEquals(2, analytics.get("totalDonors"));
        assertTrue(analytics.containsKey("bloodTypeDistribution"));
        assertTrue(analytics.containsKey("ageDemographics"));
        assertEquals(25.0, analytics.get("averageAge"));
    }

    @Test
    void shouldGenerateHospitalAnalytics() {
        Map<String, Object> analytics = reportService.generateHospitalAnalyticsByDistrict("TestDistrict");

        assertNotNull(analytics);
        assertEquals(2, analytics.get("totalHospitals"));
        assertTrue(analytics.containsKey("hospitalTypeDistribution"));
        assertTrue(analytics.containsKey("capacityRanges"));
        assertEquals(88L, analytics.get("averageCapacity"));
    }

    @Test
    void shouldGenerateCampaignAnalytics() {
        CampaignEntity campaign1 = new CampaignEntity();
        campaign1.setDistrict("District1");
        campaign1.setHospitalName("Hospital1");
        campaign1.setCreatedAt(LocalDateTime.now());

        when(campaignService.getCampaignsBetweenDates(startDate, endDate))
                .thenReturn(Arrays.asList(campaign1));

        Map<String, Object> analytics = reportService.generateCampaignAnalytics(startDate, endDate);

        assertNotNull(analytics);
        assertEquals(1, analytics.get("totalCampaigns"));
        assertTrue(analytics.containsKey("districtDistribution"));
        assertTrue(analytics.containsKey("hospitalDistribution"));
    }

    @Test
    void shouldGenerateEmergencyAnalytics() {
        EmergencyRequestEntity emergency1 = new EmergencyRequestEntity();
        emergency1.setBloodType("A+");
        emergency1.setDistrict("District1");
        emergency1.setHospital("Hospital1");
        emergency1.setUrgencyLevel("HIGH");
        emergency1.setStatus("ACTIVE");
        emergency1.setUnitsNeeded(2);
        emergency1.setCreatedAt(LocalDateTime.now());

        when(emergencyRequestService.getEmergenciesBetweenDates(startDate, endDate))
                .thenReturn(Arrays.asList(emergency1));

        Map<String, Object> analytics = reportService.generateEmergencyAnalytics(startDate, endDate);

        assertNotNull(analytics);
        assertEquals(1, analytics.get("totalEmergencies"));
        assertTrue(analytics.containsKey("bloodTypeDistribution"));
        assertTrue(analytics.containsKey("districtDistribution"));
        assertEquals(2.0, analytics.get("averageUnitsNeeded"));
    }

    @Test
    void shouldGenerateAppointmentAnalytics() {
        AppointmentEntity appointment1 = new AppointmentEntity();
        appointment1.setStatus(AppointmentEntity.AppointmentStatus.CONFIRMED);
        appointment1.setBloodType("A+");
        appointment1.setAppointmentDateTime(LocalDateTime.now());

        HospitalEntity hospital = new HospitalEntity();
        hospital.setName("Hospital1");
        appointment1.setHospital(hospital);

        when(appointmentService.getAppointmentsBetweenDates(startDate, endDate))
                .thenReturn(Arrays.asList(appointment1));

        Map<String, Object> analytics = reportService.generateAppointmentAnalytics(startDate, endDate);

        assertNotNull(analytics);
        assertEquals(1, analytics.get("totalAppointments"));
        assertTrue(analytics.containsKey("statusDistribution"));
        assertTrue(analytics.containsKey("bloodTypeDistribution"));
        assertEquals(100.0, analytics.get("attendanceRate"));
    }

    @Test
    void shouldHandleEmptyData() {
        when(donorService.getDonorsByDistrict("EmptyDistrict")).thenReturn(List.of());

        Map<String, Object> analytics = reportService.generateDonorAnalyticsByDistrict("EmptyDistrict");

        assertEquals(0, analytics.get("totalDonors"));
        assertTrue(((Map<?, ?>)analytics.get("bloodTypeDistribution")).isEmpty());
    }

    @Test
    void shouldCalculateCorrectAverages() {
        Donor donor1 = new Donor();
        donor1.setAge(20);
        Donor donor2 = new Donor();
        donor2.setAge(40);

        when(donorService.getDonorsByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(donor1, donor2));

        Map<String, Object> analytics = reportService.generateDonorAnalyticsByDistrict("TestDistrict");

        assertEquals(30.0, analytics.get("averageAge"));
    }
}