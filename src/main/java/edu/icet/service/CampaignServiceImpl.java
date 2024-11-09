package edu.icet.service;

import edu.icet.dto.CampaignRequestDTO;
import edu.icet.dto.CampaignResponseDTO;
import edu.icet.entity.Campaign;
import edu.icet.repository.CampaignDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignDao campaignRepository;
    private final FileStorageService fileStorageService;

    public List<CampaignResponseDTO> getAllCampaigns() {
        return campaignRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CampaignResponseDTO createCampaign(CampaignRequestDTO requestDTO) {
        Campaign campaign = new Campaign();
        campaign.setTitle(requestDTO.getTitle());
        campaign.setDate(requestDTO.getDate());
        campaign.setVenue(requestDTO.getVenue());
        campaign.setDistrict(requestDTO.getDistrict());
        campaign.setDescription(requestDTO.getDescription());
        campaign.setHospitalName(requestDTO.getHospitalName());
        campaign.setHospitalEmail(requestDTO.getHospitalEmail());
        campaign.setContactNumber(requestDTO.getContactNumber());

        MultipartFile image = requestDTO.getImage();
        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(image);
            campaign.setImagePath(imagePath);
        }

        Campaign savedCampaign = campaignRepository.save(campaign);
        return mapToDTO(savedCampaign);
    }

    @Override
    public void deleteCampaignById(Long id) {
        campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        campaignRepository.deleteById(id);
    }

    private CampaignResponseDTO mapToDTO(Campaign campaign) {
        CampaignResponseDTO dto = new CampaignResponseDTO();
        dto.setId(campaign.getId());
        dto.setTitle(campaign.getTitle());
        dto.setDate(campaign.getDate());
        dto.setVenue(campaign.getVenue());
        dto.setDistrict(campaign.getDistrict());
        dto.setDescription(campaign.getDescription());
        dto.setHospitalName(campaign.getHospitalName());
        dto.setContactNumber(campaign.getContactNumber());
        dto.setHospitalEmail(campaign.getHospitalEmail());
        dto.setImagePath(campaign.getImagePath());
        dto.setCreatedAt(campaign.getCreatedAt());
        return dto;
    }

    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }
    public Campaign updateCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public List<Campaign> getCampaignsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return campaignRepository.findByCreatedAtBetween(startDate, endDate);
    }

}