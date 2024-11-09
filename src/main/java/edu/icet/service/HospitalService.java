package edu.icet.service;

import edu.icet.dto.Hospital;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HospitalService {
    List<Hospital> getAll();
    void addHospital(Hospital hospital, MultipartFile profileImage);
    Hospital searchHospitalById(Long id);
    Hospital searchHospitalByName(String name);
    void deleteHospitalById(Long id);
    void updateHospital(Hospital hospital);
    Hospital getByEmail(String email);
    boolean verifyPassword(String rawPassword, String encodedPassword);
    List<Hospital> getHospitalByDistrict(String district);
    boolean hospitalExists(String emailAddress);
    void updatePassword(String email, String newPassword);
    Hospital updateProfileImage(Long donorId, String imagePath);
}
