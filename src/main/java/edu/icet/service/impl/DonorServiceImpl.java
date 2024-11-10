package edu.icet.service.impl;

import edu.icet.dto.Donor;
import edu.icet.dto.DonorEntity;
import edu.icet.repository.DonorDao;
import edu.icet.service.DonorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorServiceImpl implements DonorService {
    private final DonorDao donorDao;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(DonorServiceImpl.class);
    private final FileStorageServiceImpl fileStorageService;


    @Override
    public List<Donor> getAll() {
        ArrayList<Donor> donorArrayList = new ArrayList<>();
        donorDao.findAll().forEach(donorEntity -> {
            donorArrayList.add(modelMapper.map(donorEntity,Donor.class));
        });
        return  donorArrayList;
    }

    @Override
    public void addDonor(Donor donor, MultipartFile profileImage) {
        if (profileImage != null && !profileImage.isEmpty()) {
            String imagePath = fileStorageService.storeFile(profileImage);
            donor.setProfileImagePath(imagePath);
        }
        donor.setPassword(passwordEncoder.encode(donor.getPassword()));
        DonorEntity donorEntity = modelMapper.map(donor, DonorEntity.class);
        donorDao.save(donorEntity);
    }


    public static boolean isValidPassword(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password != null && password.matches(pattern);
    }

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
    @Transactional
    public void deleteDonorByContactNumber(String contactNumber) {
        donorDao.deleteByContactNumber(contactNumber);
    }

    @Override
    public Donor searchDonor(Integer id) {
        return modelMapper.map(donorDao.findById(id),Donor.class);
    }

    @Override
    public void deleteDonorById(Integer id) {
        donorDao.deleteById(id);
    }

    public Donor getDonorById(Integer id) {
        return modelMapper.map(donorDao.findById(id),Donor.class);
    }

    public void updateDonorById(Donor donor) {
        if (donorDao.existsById(donor.getId())) {
            donorDao.save(modelMapper.map(donor,DonorEntity.class));
        } else {
            throw new RuntimeException("Donor not found with id: " + donor.getId());
        }
    }

    @Override
    public Donor getByEmail(String email) {
        return modelMapper.map(donorDao.findByEmailAddress(email), Donor.class);
    }

    public boolean existsByEmailAddress(String email) {
        return donorDao.existsByEmailAddress(email);
    }

    public void updatePassword(String email, String newPassword) {
        DonorEntity donor = donorDao.findByEmailAddress(email);
        if (donor == null) {
            throw new RuntimeException("Donor not found");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        donor.setPassword(encodedPassword);
        donorDao.save(donor);
    }
    public Donor updateProfileImage(Integer donorId, String imagePath) {
        DonorEntity donor = donorDao.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        if (donor.getProfileImagePath() != null) {
            fileStorageService.deleteFile(donor.getProfileImagePath());
        }

        donor.setProfileImagePath(imagePath);
        DonorEntity save = donorDao.save(donor);
        return modelMapper.map(save,Donor.class);
    }

    @Override
    public List<Donor> getDonorsByDistrict(String district) {
        ArrayList<Donor> donorArrayList = new ArrayList<>();
        donorDao.findByDistrict(district).forEach(donorEntity -> {
            donorArrayList.add(modelMapper.map(donorEntity,Donor.class));
        });
        return  donorArrayList;
    }

}
