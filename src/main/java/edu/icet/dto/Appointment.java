package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private String patientName;
    private String bloodType;
    private String contactNumber;
    private String emailAddress;
    private LocalDateTime appointmentDateTime;
    private Long hospitalId;
}
