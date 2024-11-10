package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmergencyEmailRequest {
    private Long id;
    private String bloodType;
    private String district;
    private String hospitalName;
    private String urgencyLevel;
    private int unitsNeeded;
    private String contactNumber;

}
