package edu.icet.service;

import edu.icet.dto.EmergencyRequest;
import edu.icet.dto.EmergencyResponseDTO;
import edu.icet.entity.EmergencyRequestEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface EmergencyRequestService {
    EmergencyResponseDTO createRequest(EmergencyRequest requestDTO);
    List<EmergencyResponseDTO> getAllActiveRequests();
    EmergencyResponseDTO getRequestById(Long id);
    void closeRequest(Long id);
    void deleteRequest(Long id);
    EmergencyRequestEntity updateEmergencyRequest(Long id, EmergencyRequest requestDTO);
    List<EmergencyRequestEntity> getEmergenciesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
