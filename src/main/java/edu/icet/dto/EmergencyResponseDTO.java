package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmergencyResponseDTO {
    private Long id;
    private String patientName;
    private String bloodType;
    private String hospital;
    private String district;
    private String hospitalEmail;
    private String contactNumber;
    private Integer unitsNeeded;
    private String urgencyLevel;
    private String description;
    private LocalDateTime createdAt;
    private String status;
}
