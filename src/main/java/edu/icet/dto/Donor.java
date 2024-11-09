package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Donor {
    private Integer id;
    private String name;
    private LocalDate dob;
    private Integer age;
    private String bloodType;
    private String contactNumber;
    private String emailAddress;
    private String district;
    private String address;
    private String password;
    private String profileImagePath;
}
