package edu.icet.service.impl;

import edu.icet.dto.CampaignRequest;
import edu.icet.dto.CampaignResponse;
import edu.icet.entity.CampaignEntity;
import edu.icet.repository.CampaignDao;
import edu.icet.service.CampaignService;
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
    private final FileStorageServiceImpl fileStorageService;

    public List<CampaignResponse> getAllCampaigns() {
        return campaignRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest requestDTO) {
        CampaignEntity campaign = new CampaignEntity();
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

        CampaignEntity savedCampaign = campaignRepository.save(campaign);
        return mapToDTO(savedCampaign);
    }

    @Override
    public void deleteCampaignById(Long id) {
        campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppointmentEntity not found"));
        campaignRepository.deleteById(id);
    }

    private CampaignResponse mapToDTO(CampaignEntity campaign) {
        CampaignResponse dto = new CampaignResponse();
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

    public CampaignEntity getCampaignById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }
    public CampaignEntity updateCampaign(CampaignEntity campaign) {
        return campaignRepository.save(campaign);
    }

    public List<CampaignEntity> getCampaignsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return campaignRepository.findByCreatedAtBetween(startDate, endDate);
    }

}