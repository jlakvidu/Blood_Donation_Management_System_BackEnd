package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CampaignRequest {
    private String title;
    private LocalDate date;
    private String venue;
    private String district;
    private String description;
    private String hospitalName;
    private String contactNumber;
    private String hospitalEmail;
    private MultipartFile image;
}
