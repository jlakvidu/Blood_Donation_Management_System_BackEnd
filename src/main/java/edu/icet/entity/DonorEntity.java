package edu.icet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Donor")
public class DonorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private LocalDate dob;
    private Integer age;
    private String bloodType;
    private String contactNumber;
    private String emailAddress;
    private String district;
    private String address;
    @JsonIgnore
    private String password;
    private String profileImagePath;
}
