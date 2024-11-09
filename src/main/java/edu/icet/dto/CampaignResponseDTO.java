package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CampaignResponseDTO {
    private Long id;
    private String title;
    private LocalDate date;
    private String venue;
    private String district;
    private String description;
    private String hospitalName;
    private String contactNumber;
    private String hospitalEmail;
    private String imagePath;
    private LocalDateTime createdAt;
}