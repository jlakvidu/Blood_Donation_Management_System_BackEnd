package edu.icet.repository;

import edu.icet.entity.EmergencyEmailRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyEmailRequestDao extends JpaRepository<EmergencyEmailRequest,Long> {
}
