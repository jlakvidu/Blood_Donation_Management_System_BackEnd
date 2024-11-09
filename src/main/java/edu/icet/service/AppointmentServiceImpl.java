package edu.icet.service;

import edu.icet.dto.AppointmentDTO;
import edu.icet.entity.Appointment;
import edu.icet.entity.HospitalEntity;
import edu.icet.repository.AppointmentDao;
import edu.icet.repository.HospitalDao;
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

    public Appointment createAppointment(AppointmentDTO appointmentDTO) {
        HospitalEntity hospital = hospitalDao.findById(appointmentDTO.getHospitalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Appointment appointment = new Appointment();
        appointment.setPatientName(appointmentDTO.getPatientName());
        appointment.setBloodType(appointmentDTO.getBloodType());
        appointment.setContactNumber(appointmentDTO.getContactNumber());
        appointment.setEmailAddress(appointmentDTO.getEmailAddress());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setHospital(hospital);

        return appointmentDao.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDao.findAll();
    }

    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment appointment = appointmentDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        appointment.setStatus(status);
        return appointmentDao.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointmentDao.deleteById(id);
    }
    public List<Appointment> getAppointmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentDao.findByAppointmentDateTimeBetween(startDate, endDate);
    }

}
