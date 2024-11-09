package edu.icet.controller;

import edu.icet.dto.EmergencyRequestDTO;
import edu.icet.dto.EmergencyResponseDTO;
import edu.icet.entity.EmergencyRequest;
import edu.icet.service.EmergencyRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/emergency")
@Slf4j
public class EmergencyRequestController {

    private final EmergencyRequestService emergencyRequestService;


    @PostMapping
    public ResponseEntity<EmergencyResponseDTO> createEmergencyRequest(
            @RequestBody EmergencyRequestDTO requestDTO) {
        EmergencyResponseDTO response = emergencyRequestService.createRequest(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EmergencyResponseDTO>> getAllActiveRequests() {
        List<EmergencyResponseDTO> requests = emergencyRequestService.getAllActiveRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyResponseDTO> getRequestById(@PathVariable Long id) {
        EmergencyResponseDTO request = emergencyRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeRequest(@PathVariable Long id) {
        emergencyRequestService.closeRequest(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        emergencyRequestService.deleteRequest(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmergencyRequest(
            @PathVariable Long id,
            @ModelAttribute EmergencyRequestDTO requestDTO) {
        try {
            EmergencyRequest updatedRequest = emergencyRequestService.updateEmergencyRequest(id, requestDTO);
            return ResponseEntity.ok(updatedRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating emergency request: " + e.getMessage());
        }
    }
}
