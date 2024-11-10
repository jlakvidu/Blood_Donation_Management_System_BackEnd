package edu.icet.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    Map<String, Object> generateDonorAnalyticsByDistrict(String district);
    Map<String, Object> generateHospitalAnalyticsByDistrict(String district);
    Map<String, Object> generateCampaignAnalytics(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> generateEmergencyAnalytics(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> generateAppointmentAnalytics(LocalDateTime startDate, LocalDateTime endDate);
}
