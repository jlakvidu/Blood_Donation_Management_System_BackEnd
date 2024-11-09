package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Hospital {
    private Long id;
    private String name;
    private String registrationNumber;

    private String type;
    private String district;
    private String address;
    private String emailAddress;
    private String contactNumber;
    private String bloodBankLicenseNumber;
    private String bloodBankCapacity;
    private String operatingDaysAndHours;
    private String specialInstructions;
    private String password;
    private String profileImagePath;
}
