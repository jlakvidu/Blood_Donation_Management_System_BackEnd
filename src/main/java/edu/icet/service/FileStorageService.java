package edu.icet.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file);
    void deleteFile(String filePath);
    String storeDonorProfileImage(MultipartFile file);
    String storeHospitalProfileImage(MultipartFile file);
}
