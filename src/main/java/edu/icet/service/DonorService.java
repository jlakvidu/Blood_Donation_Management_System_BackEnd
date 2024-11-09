package edu.icet.service;

import edu.icet.dto.Donor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DonorService {
    List<Donor> getAll();
    void addDonor(Donor donor, MultipartFile profileImage);
    Donor searchDonor(Integer id);
    void deleteDonorById(Integer id);
    Donor getDonorById(Integer id);
    void updateDonorById(Donor donor);
    Donor getByEmail(String email);
    boolean verifyPassword(String rawPassword, String encodedPassword);
    void deleteDonorByContactNumber(String contactNumber);
    boolean existsByEmailAddress(String email);
    void updatePassword(String email, String newPassword);
    Donor updateProfileImage(Integer donorId, String imagePath);
    List<Donor> getDonorsByDistrict(String district);
}
