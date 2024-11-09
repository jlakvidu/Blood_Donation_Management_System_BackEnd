package edu.icet.service;

import edu.icet.dto.EmergencyRequestDTO;
import edu.icet.dto.EmergencyResponseDTO;
import edu.icet.entity.EmergencyRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EmergencyRequestService {
    EmergencyResponseDTO createRequest(EmergencyRequestDTO requestDTO);
    List<EmergencyResponseDTO> getAllActiveRequests();
    EmergencyResponseDTO getRequestById(Long id);
    void closeRequest(Long id);
    void deleteRequest(Long id);
    EmergencyRequest updateEmergencyRequest(Long id, EmergencyRequestDTO requestDTO);
    List<EmergencyRequest> getEmergenciesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
