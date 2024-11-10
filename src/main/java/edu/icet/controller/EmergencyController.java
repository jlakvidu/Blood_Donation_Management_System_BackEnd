package edu.icet.controller;

import edu.icet.entity.EmergencyEmailRequestEntity;
import edu.icet.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api")
@CrossOrigin
public class EmergencyController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/notify-donors")
    public ResponseEntity<?> notifyDonors(@RequestBody EmergencyEmailRequestEntity request) {
        System.out.println("Received request: " + request);
        try {
            int notifiedCount = emailService.notifyMatchingDonors(request);
            Map<String, Integer> response = new HashMap<>();
            response.put("notifiedCount", notifiedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
