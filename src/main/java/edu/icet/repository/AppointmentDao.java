package edu.icet.repository;

import edu.icet.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentDao extends JpaRepository<AppointmentEntity,Long> {
    List<AppointmentEntity> findByAppointmentDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
