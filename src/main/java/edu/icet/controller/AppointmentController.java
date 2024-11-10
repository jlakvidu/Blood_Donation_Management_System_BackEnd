package edu.icet.controller;

import edu.icet.dto.Appointment;
import edu.icet.entity.AppointmentEntity;
import edu.icet.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
@Slf4j
@CrossOrigin
public class AppointmentController {
    private final AppointmentService  appointmentService;

    @PostMapping("/create-appointments")
    public ResponseEntity<AppointmentEntity> createAppointment(@RequestBody Appointment appointment) {
        return new ResponseEntity<>(appointmentService.createAppointment(appointment), HttpStatus.CREATED);
    }

    @GetMapping("/get-appointments")
    public List<AppointmentEntity> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentEntity> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        AppointmentEntity.AppointmentStatus newStatus =
                AppointmentEntity.AppointmentStatus.valueOf(statusUpdate.get("status"));
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, newStatus));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
