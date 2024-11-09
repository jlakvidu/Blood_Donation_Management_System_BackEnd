package edu.icet.controller;

import edu.icet.service.DonorService;
import edu.icet.service.EmailService;
import edu.icet.service.OTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donor/password")
@Slf4j
@CrossOrigin
public class DonorPasswordResetController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private DonorService donorService;

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("emailAddress");
            if (donorService.existsByEmailAddress(email)) {
                emailService.sendPasswordResetOTP(email);
                return ResponseEntity.ok("OTP sent successfully");
            }
            return ResponseEntity.status(404).body("Donor not found");
        } catch (Exception e) {
            log.error("Error in password reset request", e);
            return ResponseEntity.status(500).body("Error sending OTP");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        String email = request.get("emailAddress");
        String otp = request.get("otp");

        if (otpService.validateOTP(email, otp)) {
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.status(400).body("Invalid or expired OTP");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("emailAddress");
        String newPassword = request.get("newPassword");

        try {
            donorService.updatePassword(email, newPassword);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            log.error("Error in password reset", e);
            return ResponseEntity.status(500).body("Error updating password");
        }
    }
}