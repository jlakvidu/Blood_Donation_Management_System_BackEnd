package edu.icet.service;

import edu.icet.dto.AppointmentDTO;
import edu.icet.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(AppointmentDTO appointmentDTO);
    List<Appointment> getAllAppointments();
    Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status);
    void deleteAppointment(Long id);
    List<Appointment> getAppointmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
