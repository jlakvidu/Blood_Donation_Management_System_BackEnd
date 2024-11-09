package edu.icet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Hospital")
public class HospitalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @JsonIgnore
    private String password;
    private String profileImagePath;
}
