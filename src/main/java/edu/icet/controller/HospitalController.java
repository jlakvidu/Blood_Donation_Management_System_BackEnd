package edu.icet.controller;

import edu.icet.dto.Hospital;
import edu.icet.dto.LoginRequest;
import edu.icet.service.EmailService;
import edu.icet.service.FileStorageService;
import edu.icet.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/hospital")
@Slf4j
public class HospitalController {
    private final HospitalService hospitalService;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;

    @PostMapping("/add-hospital")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHospital(
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam("name") String name,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam("type") String type,
            @RequestParam("district") String district,
            @RequestParam("address") String address,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("bloodBankLicenseNumber") String bloodBankLicenseNumber,
            @RequestParam("bloodBankCapacity") String bloodBankCapacity,
            @RequestParam("operatingDaysAndHours") String operatingDaysAndHours,
            @RequestParam("specialInstructions") String specialInstructions,
            @RequestParam("password") String password
    ) {
        Hospital hospital = new Hospital();
        hospital.setName(name);
        hospital.setRegistrationNumber(registrationNumber);
        hospital.setType(type);
        hospital.setDistrict(district);
        hospital.setAddress(address);
        hospital.setEmailAddress(emailAddress);
        hospital.setContactNumber(contactNumber);
        hospital.setBloodBankLicenseNumber(bloodBankLicenseNumber);
        hospital.setBloodBankCapacity(bloodBankCapacity);
        hospital.setOperatingDaysAndHours(operatingDaysAndHours);
        hospital.setSpecialInstructions(specialInstructions);
        hospital.setPassword(password);

        hospitalService.addHospital(hospital,profileImage);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for email: " + loginRequest.getEmailAddress());
            Hospital hospital = hospitalService.getByEmail(loginRequest.getEmailAddress());
            if (hospital == null) {
                System.out.println("Hospital not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not found");
            }
            boolean passwordMatch = hospitalService.verifyPassword(
                    loginRequest.getPassword(),
                    hospital.getPassword()
            );
            System.out.println("Password match: " + passwordMatch);

            if (passwordMatch) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/profile/{emailAddress}")
    public ResponseEntity<?> getHospitalProfile(@PathVariable String emailAddress) {
        try {
            Hospital hospital = hospitalService.getByEmail(emailAddress);
            if (hospital != null) {
                return ResponseEntity.ok(hospital);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching hospital profile");
        }
    }

    @GetMapping("/get-all")
    private List<Hospital> getAll(){
        return hospitalService.getAll();
    }

    @PutMapping("/update-hospital")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateHospital(@RequestBody Hospital hospital) {
        try {
            hospitalService.updateHospital(hospital);
            try {
                int capacity = Integer.parseInt(hospital.getBloodBankCapacity());
                if (capacity < 20) {
                    emailService.notifyDonorsForLowCapacity(hospital.getDistrict());
                }
            } catch (NumberFormatException e) {
                log.error("Error parsing blood bank capacity: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error updating hospital: " + e.getMessage());
            throw new RuntimeException("Failed to update hospital: " + e.getMessage());
        }
    }

    @GetMapping("/search-hospital-by-id/{id}")
    private Hospital searchHospitalById(@PathVariable Long id){
        return hospitalService.searchHospitalById(id);
    }

    @GetMapping("/search-hospital-by-name/{name}")
    private Hospital searchHospitalByName(@PathVariable String name){
        return hospitalService.searchHospitalByName(name);
    }

    @GetMapping("/find-by-emailAddress/{emailAddress}")
    private Hospital findByEmailAddress(@PathVariable String emailAddress){
        return hospitalService.getByEmail(emailAddress);
    }

    @DeleteMapping("/delete-hospital/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    private void deleteHospitalById(@PathVariable Long id){
        hospitalService.deleteHospitalById(id);
    }

    @GetMapping("/get-hospital-by-district/{district}")
    private List<Hospital> getHospitalByDistrict(@PathVariable String district){
        return hospitalService.getHospitalByDistrict(district);
    }

    @GetMapping("/details/{emailAddress}")
    public ResponseEntity<?> getHospitalDetails(@PathVariable String emailAddress) {
        try {
            Hospital hospital = hospitalService.getByEmail(emailAddress);
            if (hospital == null) {
                return new ResponseEntity<>("Hospital not found", HttpStatus.NOT_FOUND);
            }

            Map<String, String> details = new HashMap<>();
            details.put("hospitalName", hospital.getName());
            details.put("contactNumber", hospital.getContactNumber());
            details.put("district",hospital.getDistrict());
            details.put("emailAddress", hospital.getEmailAddress());

            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching hospital details: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hospitalId") Long hospitalId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            long maxSize = 10 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest().body("File size should be less than 10MB");
            }

            String filename = fileStorageService.storeFile(file);
            hospitalService.updateProfileImage(hospitalId, filename);
            Map<String, String> response = new HashMap<>();
            response.put("profileImagePath", "/uploads/" + filename);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + e.getMessage());
        }
    }
}
