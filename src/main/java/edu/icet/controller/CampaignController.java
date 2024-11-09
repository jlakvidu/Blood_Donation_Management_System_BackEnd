package edu.icet.controller;

import edu.icet.dto.CampaignRequestDTO;
import edu.icet.dto.CampaignResponseDTO;
import edu.icet.entity.Campaign;
import edu.icet.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
@Slf4j
@CrossOrigin
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    public ResponseEntity<List<CampaignResponseDTO>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @PostMapping
    public ResponseEntity<CampaignResponseDTO> createCampaign(
            @RequestParam("title") String title,
            @RequestParam("date") String date,
            @RequestParam("venue") String venue,
            @RequestParam("district") String district,
            @RequestParam("description") String description,
            @RequestParam("hospitalName") String hospitalName,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("hospitalEmail") String hospitalEmail,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        CampaignRequestDTO requestDTO = new CampaignRequestDTO();
        requestDTO.setTitle(title);
        requestDTO.setDate(LocalDate.parse(date));
        requestDTO.setVenue(venue);
        requestDTO.setDistrict(district);
        requestDTO.setDescription(description);
        requestDTO.setHospitalName(hospitalName);
        requestDTO.setContactNumber(contactNumber);
        requestDTO.setHospitalEmail(hospitalEmail);
        requestDTO.setImage(image);

        return ResponseEntity.ok(campaignService.createCampaign(requestDTO));
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    private void deleteCampaignById(@PathVariable Long id){
        campaignService.deleteCampaignById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id, @RequestBody Campaign campaign) {
        try {
            Campaign existingCampaign = campaignService.getCampaignById(id);

            if (existingCampaign == null) {
                return new ResponseEntity<>("Campaign not found", HttpStatus.NOT_FOUND);
            }

            existingCampaign.setTitle(campaign.getTitle());
            existingCampaign.setDate(campaign.getDate());
            existingCampaign.setVenue(campaign.getVenue());
            existingCampaign.setDescription(campaign.getDescription());

            Campaign updatedCampaign = campaignService.updateCampaign(existingCampaign);

            return new ResponseEntity<>(updatedCampaign, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating campaign: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
