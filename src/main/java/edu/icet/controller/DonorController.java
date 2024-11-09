package edu.icet.controller;

import edu.icet.dto.Donor;
import edu.icet.dto.LoginRequest;
import edu.icet.service.DonorService;
import edu.icet.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donor")
@Slf4j
@CrossOrigin
public class DonorController {

    private final DonorService donorService;
    private final FileStorageService fileStorageService;

    @PostMapping("/add-donor")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDonor(
            @RequestParam("name") String name,
            @RequestParam("dob") String dob,
            @RequestParam("age") Integer age,
            @RequestParam("bloodType") String bloodType,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("district") String district,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Donor donor = new Donor();
        donor.setName(name);
        donor.setDob(LocalDate.parse(dob));
        donor.setAge(age);
        donor.setBloodType(bloodType);
        donor.setContactNumber(contactNumber);
        donor.setEmailAddress(emailAddress);
        donor.setDistrict(district);
        donor.setAddress(address);
        donor.setPassword(password);

        log.info("Received Donor request with image: {}", donor);
        donorService.addDonor(donor, profileImage);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Add debug logging
            System.out.println("Login attempt for email: " + loginRequest.getEmailAddress());

            Donor donor = donorService.getByEmail(loginRequest.getEmailAddress());

            if (donor == null) {
                System.out.println("Donor not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not found");
            }

            boolean passwordMatch = donorService.verifyPassword(
                    loginRequest.getPassword(),
                    donor.getPassword()
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

    @GetMapping("/get-all")
    public List<Donor> getAll(){
        return donorService.getAll();
    }

    @GetMapping("/search-donor-by-id/{id}")
    public Donor searchDonor(@PathVariable Integer id){
        return donorService.searchDonor(id);
    }

    @GetMapping("/profile/{emailAddress}")
    public ResponseEntity<?> getDonorProfile(@PathVariable String emailAddress) {
        try {
            // Fetch donor details from your database using the email
            Donor donor = donorService.getByEmail(emailAddress);
            if (donor != null) {
                return ResponseEntity.ok(donor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching donor profile");
        }
    }

    @DeleteMapping("/delete-donor-by-id/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteDonorById(@PathVariable Integer id){
        donorService.deleteDonorById(id);
    }

    @DeleteMapping("/delete-by-number/{contactNumber}")
    public ResponseEntity<?> deleteDonorByContactNumber(@PathVariable String contactNumber) {
        try {
            donorService.deleteDonorByContactNumber(contactNumber);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting donor: " + e.getMessage());
        }
    }

    @PutMapping("/update-donor")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateDonor(@RequestBody Donor donor) {
        try {
            Donor existingDonor = donorService.getDonorById(donor.getId());
            if (existingDonor == null) {
                return ResponseEntity.notFound().build();
            }
            donor.setProfileImagePath(existingDonor.getProfileImagePath());
            donorService.updateDonorById(donor);
            return ResponseEntity.accepted().body("Donor updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating donor: " + e.getMessage());
        }
    }



    @GetMapping("/profile-image/{email}")
    public ResponseEntity<?> getProfileImage(@PathVariable String email) {
        Donor donor = donorService.getByEmail(email);
        if (donor != null && donor.getProfileImagePath() != null) {
            return ResponseEntity.ok(donor.getProfileImagePath());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("donorId") Integer donorId) {
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
            donorService.updateProfileImage(donorId, filename);
            Map<String, String> response = new HashMap<>();
            response.put("profileImagePath", "/uploads/" + filename);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + e.getMessage());
        }
    }

    @GetMapping("/donors-by-district/{district}")
    private List<Donor> getDonorsByDistrict(@PathVariable String district ){
        return donorService.getDonorsByDistrict(district);
    }
}
