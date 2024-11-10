package edu.icet.service.impl;

import edu.icet.service.OTPService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class OTPServiceImpl implements OTPService {
    private Map<String, OTPData> otpMap = new ConcurrentHashMap<>();

    @Value("${otp.expiry.minutes:5}")
    private int expiryMinutes;

    public String generateOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpMap.put(email, new OTPData(otp, LocalDateTime.now()));
        return otp;
    }

    public boolean validateOTP(String email, String otp) {
        OTPData otpData = otpMap.get(email);
        if (otpData == null) return false;

        boolean isValid = otpData.otp().equals(otp) &&
                LocalDateTime.now().minusMinutes(expiryMinutes).isBefore(otpData.timestamp());

        if (isValid) {
            otpMap.remove(email);
        }

        return isValid;
    }

    record OTPData(String otp, LocalDateTime timestamp) {}
}
