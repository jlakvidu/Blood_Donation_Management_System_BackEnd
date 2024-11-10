package edu.icet.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class OTPServiceImplTest {

    private OTPServiceImpl otpService;
    private static final String TEST_EMAIL = "test@test.com";

    @BeforeEach
    void setUp() {
        otpService = new OTPServiceImpl();
        ReflectionTestUtils.setField(otpService, "expiryMinutes", 5);
        ReflectionTestUtils.setField(otpService, "otpMap", new ConcurrentHashMap<>());
    }

    @Test
    void shouldGenerateOTP() {
        String otp = otpService.generateOTP(TEST_EMAIL);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }

    @Test
    void shouldGenerateDifferentOTPsForSameEmail() {
        String firstOTP = otpService.generateOTP(TEST_EMAIL);
        String secondOTP = otpService.generateOTP(TEST_EMAIL);

        assertNotEquals(firstOTP, secondOTP);
    }

    @Test
    void shouldValidateCorrectOTP() {
        String otp = otpService.generateOTP(TEST_EMAIL);

        assertTrue(otpService.validateOTP(TEST_EMAIL, otp));
    }

    @Test
    void shouldNotValidateIncorrectOTP() {
        otpService.generateOTP(TEST_EMAIL);

        assertFalse(otpService.validateOTP(TEST_EMAIL, "000000"));
    }

    @Test
    void shouldNotValidateExpiredOTP() {
        String otp = otpService.generateOTP(TEST_EMAIL);
        Map<String, OTPServiceImpl.OTPData> otpMap = (Map<String, OTPServiceImpl.OTPData>)
                ReflectionTestUtils.getField(otpService, "otpMap");

        otpMap.put(TEST_EMAIL, new OTPServiceImpl.OTPData(otp, LocalDateTime.now().minusMinutes(6)));

        assertFalse(otpService.validateOTP(TEST_EMAIL, otp));
    }

    @Test
    void shouldNotValidateNonExistentOTP() {
        assertFalse(otpService.validateOTP(TEST_EMAIL, "123456"));
    }

    @Test
    void shouldRemoveOTPAfterSuccessfulValidation() {
        String otp = otpService.generateOTP(TEST_EMAIL);

        assertTrue(otpService.validateOTP(TEST_EMAIL, otp));
        assertFalse(otpService.validateOTP(TEST_EMAIL, otp));
    }

    @Test
    void shouldHandleMultipleEmailsSimultaneously() {
        String email1 = "test1@test.com";
        String email2 = "test2@test.com";

        String otp1 = otpService.generateOTP(email1);
        String otp2 = otpService.generateOTP(email2);

        assertTrue(otpService.validateOTP(email1, otp1));
        assertTrue(otpService.validateOTP(email2, otp2));
    }

    @Test
    void shouldGenerateNumericOTPOnly() {
        String otp = otpService.generateOTP(TEST_EMAIL);

        assertTrue(otp.matches("\\d+"));
    }

    @Test
    void shouldMaintainOTPLength() {
        for (int i = 0; i < 100; i++) {
            String otp = otpService.generateOTP(TEST_EMAIL);
            assertEquals(6, otp.length());
        }
    }
}