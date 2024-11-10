package edu.icet.service;

import edu.icet.dto.CampaignRequest;
import edu.icet.dto.CampaignResponse;
import edu.icet.entity.CampaignEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignService {
    List<CampaignResponse> getAllCampaigns();
    CampaignResponse createCampaign(CampaignRequest requestDTO);
    void deleteCampaignById(Long id);
    CampaignEntity updateCampaign(CampaignEntity campaign);
    CampaignEntity getCampaignById(Long id);
    List<CampaignEntity> getCampaignsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
