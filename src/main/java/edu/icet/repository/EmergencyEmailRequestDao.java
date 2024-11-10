package edu.icet.repository;

import edu.icet.entity.EmergencyEmailRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyEmailRequestDao extends JpaRepository<EmergencyEmailRequestEntity,Long> {
}
