package edu.icet.service;

import edu.icet.dto.CampaignRequestDTO;
import edu.icet.dto.CampaignResponseDTO;
import edu.icet.entity.Campaign;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignService {
    List<CampaignResponseDTO> getAllCampaigns();
    CampaignResponseDTO createCampaign(CampaignRequestDTO requestDTO);
    void deleteCampaignById(Long id);
    Campaign updateCampaign(Campaign campaign);
    Campaign getCampaignById(Long id);
    List<Campaign> getCampaignsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
