package edu.icet.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emergency_requests")
public class EmergencyRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String bloodType;

    @Column(nullable = false)
    private String hospital;

    private String district;

    private String hospitalEmail;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private Integer unitsNeeded;

    @Column(nullable = false)
    private String urgencyLevel;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String status = "ACTIVE";

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
