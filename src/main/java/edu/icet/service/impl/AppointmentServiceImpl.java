package edu.icet.service.impl;

import edu.icet.dto.Appointment;
import edu.icet.entity.AppointmentEntity;
import edu.icet.entity.HospitalEntity;
import edu.icet.repository.AppointmentDao;
import edu.icet.repository.HospitalDao;
import edu.icet.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final HospitalDao hospitalDao;
    private final AppointmentDao appointmentDao;
    private final ModelMapper modelMapper;

    public AppointmentEntity createAppointment(Appointment appointmentDTO) {
        HospitalEntity hospital = hospitalDao.findById(appointmentDTO.getHospitalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AppointmentEntity not found"));

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatientName(appointmentDTO.getPatientName());
        appointment.setBloodType(appointmentDTO.getBloodType());
        appointment.setContactNumber(appointmentDTO.getContactNumber());
        appointment.setEmailAddress(appointmentDTO.getEmailAddress());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setHospital(hospital);

        return appointmentDao.save(appointment);
    }

    public List<AppointmentEntity> getAllAppointments() {
        return appointmentDao.findAll();
    }

    public AppointmentEntity updateAppointmentStatus(Long id, AppointmentEntity.AppointmentStatus status) {
        AppointmentEntity appointment = appointmentDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AppointmentEntity not found"));
        appointment.setStatus(status);
        return appointmentDao.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentDao.findById(id)
                .orElseThrow(() -> new RuntimeException("AppointmentEntity not found"));
        appointmentDao.deleteById(id);
    }
    public List<AppointmentEntity> getAppointmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentDao.findByAppointmentDateTimeBetween(startDate, endDate);
    }

}
