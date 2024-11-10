package edu.icet.service.impl;

import edu.icet.dto.DonorEntity;
import edu.icet.dto.Notification;
import edu.icet.entity.EmergencyEmailRequestEntity;
import edu.icet.entity.NotificationEntity;
import edu.icet.repository.DonorDao;
import edu.icet.repository.HospitalDao;
import edu.icet.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceImplTest {

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private DonorDao donorRepository;

    @MockBean
    private HospitalDao hospitalRepository;

    @MockBean
    private OTPServiceImpl otpService;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private EmailServiceImpl emailService;

    private DonorEntity testDonor;
    private EmergencyEmailRequestEntity testRequest;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        testDonor = new DonorEntity();
        testDonor.setEmailAddress("test@test.com");
        testDonor.setBloodType("A+");
        testDonor.setDistrict("TestDistrict");

        testRequest = new EmergencyEmailRequestEntity();
        testRequest.setBloodType("A+");
        testRequest.setDistrict("TestDistrict");
        testRequest.setHospitalName("Test Hospital");
        testRequest.setUnitsNeeded(2);
        testRequest.setUrgencyLevel("HIGH");
        testRequest.setContactNumber("1234567890");

        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void shouldNotifyMatchingDonors() throws Exception {
        when(donorRepository.findByBloodTypeAndDistrict("A+", "TestDistrict"))
                .thenReturn(Arrays.asList(testDonor));

        int result = emailService.notifyMatchingDonors(testRequest);

        assertEquals(1, result);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldHandleNoMatchingDonors() {
        when(donorRepository.findByBloodTypeAndDistrict("A+", "TestDistrict"))
                .thenReturn(Collections.emptyList());

        int result = emailService.notifyMatchingDonors(testRequest);

        assertEquals(0, result);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendPasswordResetOTP() throws Exception {
        when(otpService.generateOTP("test@test.com")).thenReturn("123456");

        emailService.sendPasswordResetOTP("test@test.com");

        verify(mailSender).send(any(MimeMessage.class));
        verify(otpService).generateOTP("test@test.com");
    }

    @Test
    void shouldSendAppointmentStatusEmail() throws Exception {
        Notification notification = new Notification();
        notification.setRecipientEmail("test@test.com");
        notification.setTitle("Appointment Confirmed");
        notification.setType(NotificationEntity.NotificationType.APPOINTMENT_CONFIRMED);
        notification.setMessage("Your appointment has been confirmed");

        emailService.sendAppointmentStatusEmail(notification);

        verify(mailSender).send(any(MimeMessage.class));
        verify(notificationService).createNotification(any(Notification.class));
    }

    @Test
    void shouldNotifyDonorsForLowCapacity() throws Exception {
        when(donorRepository.findByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(testDonor));

        emailService.notifyDonorsForLowCapacity("TestDistrict");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldHandleEmailSendingFailure() {
        when(donorRepository.findByBloodTypeAndDistrict("A+", "TestDistrict"))
                .thenReturn(Arrays.asList(testDonor));
        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.notifyMatchingDonors(testRequest));
    }

    @Test
    void shouldHandleOTPGenerationFailure() {
        when(otpService.generateOTP("test@test.com"))
                .thenThrow(new RuntimeException("OTP generation failed"));

        assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetOTP("test@test.com"));
    }

    @Test
    void shouldHandleNotificationCreationFailure() {
        Notification notification = new Notification();
        notification.setRecipientEmail("test@test.com");
        notification.setType(NotificationEntity.NotificationType.APPOINTMENT_CONFIRMED);

        doThrow(new RuntimeException("Database error"))
                .when(notificationService).createNotification(any(Notification.class));

        assertThrows(RuntimeException.class, () ->
                emailService.sendAppointmentStatusEmail(notification));
    }

    @Test
    void shouldHandleLowCapacityEmailFailure() {
        when(donorRepository.findByDistrict("TestDistrict"))
                .thenReturn(Arrays.asList(testDonor));
        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(any(MimeMessage.class));

        emailService.notifyDonorsForLowCapacity("TestDistrict");

        verify(donorRepository).findByDistrict("TestDistrict");
    }
}