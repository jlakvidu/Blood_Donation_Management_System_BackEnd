package edu.icet.service.impl;

import edu.icet.dto.Appointment;
import edu.icet.entity.AppointmentEntity;
import edu.icet.entity.HospitalEntity;
import edu.icet.repository.AppointmentDao;
import edu.icet.repository.HospitalDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AppointmentServiceImplTest {

    @MockBean
    private HospitalDao hospitalDao;

    @MockBean
    private AppointmentDao appointmentDao;

    @Autowired
    private AppointmentServiceImpl appointmentService;

    private Appointment testAppointment;
    private AppointmentEntity testAppointmentEntity;
    private HospitalEntity testHospital;

    @BeforeEach
    void setUp() {
        testHospital = new HospitalEntity();
        testHospital.setId(1L);
        testHospital.setName("Test Hospital");

        testAppointment = new Appointment();
        testAppointment.setHospitalId(1L);
        testAppointment.setPatientName("Test Patient");
        testAppointment.setBloodType("A+");
        testAppointment.setContactNumber("1234567890");
        testAppointment.setEmailAddress("test@test.com");
        testAppointment.setAppointmentDateTime(LocalDateTime.now());

        testAppointmentEntity = new AppointmentEntity();
        testAppointmentEntity.setId(1L);
        testAppointmentEntity.setPatientName("Test Patient");
        testAppointmentEntity.setBloodType("A+");
        testAppointmentEntity.setContactNumber("1234567890");
        testAppointmentEntity.setEmailAddress("test@test.com");
        testAppointmentEntity.setAppointmentDateTime(LocalDateTime.now());
        testAppointmentEntity.setHospital(testHospital);
    }

    @Test
    void shouldCreateAppointment() {
        when(hospitalDao.findById(1L)).thenReturn(Optional.of(testHospital));
        when(appointmentDao.save(any(AppointmentEntity.class))).thenReturn(testAppointmentEntity);

        AppointmentEntity result = appointmentService.createAppointment(testAppointment);

        assertNotNull(result);
        assertEquals("Test Patient", result.getPatientName());
        verify(appointmentDao).save(any(AppointmentEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingAppointmentWithNonExistentHospital() {
        when(hospitalDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> appointmentService.createAppointment(testAppointment));
    }

    @Test
    void shouldGetAllAppointments() {
        when(appointmentDao.findAll()).thenReturn(Arrays.asList(testAppointmentEntity));

        List<AppointmentEntity> result = appointmentService.getAllAppointments();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(appointmentDao).findAll();
    }

    @Test
    void shouldUpdateAppointmentStatus() {
        when(appointmentDao.findById(1L)).thenReturn(Optional.of(testAppointmentEntity));
        when(appointmentDao.save(any(AppointmentEntity.class))).thenReturn(testAppointmentEntity);

        AppointmentEntity result = appointmentService.updateAppointmentStatus(1L, AppointmentEntity.AppointmentStatus.CONFIRMED);

        assertNotNull(result);
        verify(appointmentDao).save(any(AppointmentEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentAppointment() {
        when(appointmentDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> appointmentService.updateAppointmentStatus(1L, AppointmentEntity.AppointmentStatus.CONFIRMED));
    }

    @Test
    void shouldDeleteAppointment() {
        when(appointmentDao.findById(1L)).thenReturn(Optional.of(testAppointmentEntity));

        appointmentService.deleteAppointment(1L);

        verify(appointmentDao).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAppointment() {
        when(appointmentDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.deleteAppointment(1L));
    }

    @Test
    void shouldGetAppointmentsBetweenDates() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(appointmentDao.findByAppointmentDateTimeBetween(startDate, endDate))
                .thenReturn(Arrays.asList(testAppointmentEntity));

        List<AppointmentEntity> result = appointmentService.getAppointmentsBetweenDates(startDate, endDate);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(appointmentDao).findByAppointmentDateTimeBetween(startDate, endDate);
    }
}