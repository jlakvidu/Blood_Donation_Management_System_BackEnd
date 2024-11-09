package edu.icet.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            LocalDate now = LocalDate.now();
            String yearMonth = String.format("%d/%02d", now.getYear(), now.getMonthValue());
            Path yearMonthPath = Paths.get(uploadDir, "campaigns", yearMonth);
            Files.createDirectories(yearMonthPath);
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = yearMonthPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation);

            return String.format("/campaigns/%s/%s", yearMonth, newFilename);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path targetLocation = Paths.get(uploadDir).resolve(filePath.substring(1));
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file. Please try again!", ex);
        }
    }

    public String storeDonorProfileImage(MultipartFile file) {
        try {
            Path donorImagesPath = Paths.get(uploadDir, "donors", "profiles");
            Files.createDirectories(donorImagesPath);

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = donorImagesPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation);

            return String.format("/donors/profiles/%s", newFilename);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store donor profile image. Please try again!", ex);
        }
    }
    public String storeHospitalProfileImage(MultipartFile file) {
        try {
            Path hospitalImagesPath = Paths.get(uploadDir, "hospitals", "profiles");
            Files.createDirectories(hospitalImagesPath);

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = hospitalImagesPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation);

            return String.format("/hospitals/profiles/%s", newFilename);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store hospital profile image. Please try again!", ex);
        }
    }
}
