package edu.icet.service;

import edu.icet.dto.Appointment;
import edu.icet.entity.AppointmentEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentEntity createAppointment(Appointment appointmentDTO);
    List<AppointmentEntity> getAllAppointments();
    AppointmentEntity updateAppointmentStatus(Long id, AppointmentEntity.AppointmentStatus status);
    void deleteAppointment(Long id);
    List<AppointmentEntity> getAppointmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
