package edu.icet.service;

import edu.icet.dto.Donor;
import edu.icet.dto.DonorEntity;
import edu.icet.dto.Hospital;
import edu.icet.entity.HospitalEntity;
import edu.icet.repository.HospitalDao;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService{
    private final HospitalDao hospitalDao;
    final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    public List<Hospital> getAll() {
        ArrayList<Hospital> hospitalArrayList = new ArrayList<>();
        hospitalDao.findAll().forEach(hospitalEntity -> {
            hospitalArrayList.add(modelMapper.map(hospitalEntity,Hospital.class));
        });
        return  hospitalArrayList;
    }

    @Override
    public void addHospital(Hospital hospital, MultipartFile profileImage) {
        if (profileImage != null && !profileImage.isEmpty()) {
            String imagePath = fileStorageService.storeFile(profileImage);
            hospital.setProfileImagePath(imagePath);
        }
        hospital.setPassword(passwordEncoder.encode(hospital.getPassword()));
        hospitalDao.save(modelMapper.map(hospital, HospitalEntity.class));
    }

    public static boolean isValidPassword(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password != null && password.matches(pattern);
    }

    @Override
    public Hospital searchHospitalById(Long id) {
        return modelMapper.map(hospitalDao.findById(id),Hospital.class);
    }

    @Override
    public Hospital searchHospitalByName(String name) {
        return modelMapper.map(hospitalDao.findByName(name),Hospital.class);
    }

    @Override
    public boolean verifyPassword(String inputPassword, String storedPassword) {
        System.out.println("Verifying password");
        try {
            return BCrypt.checkpw(inputPassword, storedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Hospital> getHospitalByDistrict(String district) {
        ArrayList<Hospital> hospitalArrayList = new ArrayList<>();
        hospitalDao.findByDistrict(district).forEach(hospitalEntity -> {
            hospitalArrayList.add(modelMapper.map(hospitalEntity,Hospital.class));
        });
        return  hospitalArrayList;
    }

    @Override
    public void deleteHospitalById(Long id) {
        hospitalDao.deleteById(id);
    }

    @Override
    public void updateHospital(Hospital hospital) {
        hospitalDao.save(modelMapper.map(hospital, HospitalEntity.class));
    }

    @Override
    public Hospital getByEmail(String email) {
        return modelMapper.map(hospitalDao.findByEmailAddress(email), Hospital.class);
    }

    @Override
    public boolean hospitalExists(String emailAddress) {
        return hospitalDao.existsByEmailAddress(emailAddress);
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        HospitalEntity hospital = hospitalDao.findByEmailAddress(email);
        if (hospital == null) {
            throw new RuntimeException("Hospital not found");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        hospital.setPassword(encodedPassword);
        hospitalDao.save(hospital);
    }

    public Hospital updateProfileImage(Long donorId, String imagePath) {
        HospitalEntity hospital = hospitalDao.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        if (hospital.getProfileImagePath() != null) {
            fileStorageService.deleteFile(hospital.getProfileImagePath());
        }

        hospital.setProfileImagePath(imagePath);
        HospitalEntity save = hospitalDao.save(hospital);
        return modelMapper.map(save,Hospital.class);
    }
}
