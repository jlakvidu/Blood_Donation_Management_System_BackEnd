package edu.icet.service;

import edu.icet.dto.EmergencyRequestDTO;
import edu.icet.dto.EmergencyResponseDTO;
import edu.icet.entity.EmergencyRequest;
import edu.icet.repository.EmergencyRequestDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyRequestServiceImpl implements EmergencyRequestService{
    private final EmergencyRequestDao emergencyRequestRepository;
    private final FileStorageService fileStorageService;

    @Override
    public EmergencyResponseDTO createRequest(EmergencyRequestDTO requestDTO) {
        EmergencyRequest request = new EmergencyRequest();
        request.setPatientName(requestDTO.getPatientName());
        request.setBloodType(requestDTO.getBloodType());
        request.setHospital(requestDTO.getHospital());
        request.setDistrict(requestDTO.getDistrict());
        request.setHospitalEmail(requestDTO.getHospitalEmail());
        request.setContactNumber(requestDTO.getContactNumber());
        request.setUnitsNeeded(requestDTO.getUnitsNeeded());
        request.setUrgencyLevel(requestDTO.getUrgencyLevel());
        request.setDescription(requestDTO.getDescription());
        EmergencyRequest savedRequest = emergencyRequestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    @Override
    public List<EmergencyResponseDTO> getAllActiveRequests() {
        return emergencyRequestRepository.findByStatusOrderByCreatedAtDesc("ACTIVE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyResponseDTO getRequestById(Long id) {
        EmergencyRequest request = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency request not found"));
        return convertToDTO(request);
    }

    @Override
    public void closeRequest(Long id) {
        EmergencyRequest request = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency request not found"));
        request.setStatus("CLOSED");
        emergencyRequestRepository.save(request);
    }

    @Override
    public void deleteRequest(Long id) {
        EmergencyRequest request = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency request not found"));
        emergencyRequestRepository.delete(request);
    }

    private EmergencyResponseDTO convertToDTO(EmergencyRequest request) {
        EmergencyResponseDTO dto = new EmergencyResponseDTO();
        BeanUtils.copyProperties(request, dto);
        return dto;
    }

    public EmergencyRequest updateEmergencyRequest(Long id, EmergencyRequestDTO requestDTO) {
        EmergencyRequest existingRequest = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency request not found with id: " + id));
        existingRequest.setPatientName(requestDTO.getPatientName());
        existingRequest.setBloodType(requestDTO.getBloodType());
        existingRequest.setContactNumber(requestDTO.getContactNumber());
        existingRequest.setUnitsNeeded(requestDTO.getUnitsNeeded());
        existingRequest.setUrgencyLevel(requestDTO.getUrgencyLevel());
        existingRequest.setDescription(requestDTO.getDescription());

        return emergencyRequestRepository.save(existingRequest);
    }

    public List<EmergencyRequest> getEmergenciesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return emergencyRequestRepository.findByCreatedAtBetween(startDate, endDate);
    }
}
