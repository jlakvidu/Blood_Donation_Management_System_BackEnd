package edu.icet.controller;

import edu.icet.service.EmailService;
import edu.icet.service.HospitalService;
import edu.icet.service.OTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hospital/password")
@Slf4j
@CrossOrigin
public class PasswordResetController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("emailAddress");
            if (hospitalService.hospitalExists(email)) {
                emailService.sendPasswordResetOTP(email);
                return ResponseEntity.ok("OTP sent successfully");
            }
            return ResponseEntity.status(404).body("Hospital not found");
        } catch (Exception e) {
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
            hospitalService.updatePassword(email, newPassword);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating password");
        }
    }

}
