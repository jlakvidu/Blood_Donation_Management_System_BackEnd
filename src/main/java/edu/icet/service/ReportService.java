package edu.icet.service;

import edu.icet.dto.Donor;
import edu.icet.dto.Hospital;
import edu.icet.entity.Appointment;
import edu.icet.entity.Campaign;
import edu.icet.entity.EmergencyRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final DonorService donorService;
    private final HospitalService hospitalService;
    private final CampaignService campaignService;
    private final EmergencyRequestService emergencyRequestService;
    private final AppointmentService appointmentService;

    public Map<String, Object> generateDonorAnalyticsByDistrict(String district) {
        List<Donor> donors = donorService.getDonorsByDistrict(district);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalDonors", donors.size());
        Map<String, Long> bloodTypeDistribution = donors.stream()
                .collect(Collectors.groupingBy(Donor::getBloodType, Collectors.counting()));
        analytics.put("bloodTypeDistribution", bloodTypeDistribution);
        Map<String, Long> ageDemographics = donors.stream()
                .collect(Collectors.groupingBy(donor -> {
                    int age = donor.getAge();
                    if (age < 25) return "18-24";
                    if (age < 35) return "25-34";
                    if (age < 45) return "35-44";
                    return "45+";
                }, Collectors.counting()));
        analytics.put("ageDemographics", ageDemographics);

        // Gender distribution (if you have gender field)
        // Map<String, Long> genderDistribution = donors.stream()
        //         .collect(Collectors.groupingBy(Donor::getGender, Collectors.counting()));
        // analytics.put("genderDistribution", genderDistribution);

        double averageAge = donors.stream()
                .mapToInt(Donor::getAge)
                .average()
                .orElse(0.0);
        analytics.put("averageAge", Math.round(averageAge * 100.0) / 100.0);
        Optional<Map.Entry<String, Long>> mostCommonBloodType = bloodTypeDistribution.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        mostCommonBloodType.ifPresent(entry ->
                analytics.put("mostCommonBloodType", entry.getKey())
        );

        Map<String, Double> bloodTypePercentages = bloodTypeDistribution.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round((e.getValue() * 100.0 / donors.size()) * 100.0) / 100.0
                ));
        analytics.put("bloodTypePercentages", bloodTypePercentages);
        analytics.put("district", district);
        return analytics;
    }

    public Map<String, Object> generateHospitalAnalyticsByDistrict(String district) {
        List<Hospital> hospitals = hospitalService.getHospitalByDistrict(district);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalHospitals", hospitals.size());
        Map<String, Long> hospitalTypeDistribution = hospitals.stream()
                .collect(Collectors.groupingBy(Hospital::getType, Collectors.counting()));
        analytics.put("hospitalTypeDistribution", hospitalTypeDistribution);
        Map<String, Long> capacityRanges = hospitals.stream()
                .collect(Collectors.groupingBy(hospital -> {
                    int capacity = Integer.parseInt(hospital.getBloodBankCapacity());
                    if (capacity < 50) return "Low (0-50)";
                    if (capacity < 100) return "Medium (51-100)";
                    return "High (100+)";
                }, Collectors.counting()));
        analytics.put("capacityRanges", capacityRanges);
        double averageCapacity = hospitals.stream()
                .mapToInt(h -> Integer.parseInt(h.getBloodBankCapacity()))
                .average()
                .orElse(0.0);
        analytics.put("averageCapacity", Math.round(averageCapacity));
        analytics.put("district", district);
        return analytics;
    }

    public Map<String, Object> generateCampaignAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Campaign> campaigns = campaignService.getCampaignsBetweenDates(startDate, endDate);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalCampaigns", campaigns.size());
        Map<String, Long> districtDistribution = campaigns.stream()
                .collect(Collectors.groupingBy(Campaign::getDistrict, Collectors.counting()));
        analytics.put("districtDistribution", districtDistribution);
        Map<String, Long> hospitalDistribution = campaigns.stream()
                .collect(Collectors.groupingBy(Campaign::getHospitalName, Collectors.counting()));
        analytics.put("hospitalDistribution", hospitalDistribution);
        Map<String, Long> monthlyDistribution = campaigns.stream()
                .collect(Collectors.groupingBy(
                        campaign -> campaign.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        Collectors.counting()
                ));
        analytics.put("monthlyDistribution", monthlyDistribution);
        analytics.put("startDate", startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        analytics.put("endDate", endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return analytics;
    }

    public Map<String, Object> generateEmergencyAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<EmergencyRequest> emergencies = emergencyRequestService.getEmergenciesBetweenDates(startDate, endDate);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEmergencies", emergencies.size());
        Map<String, Long> bloodTypeDistribution = emergencies.stream()
                .collect(Collectors.groupingBy(EmergencyRequest::getBloodType, Collectors.counting()));
        analytics.put("bloodTypeDistribution", bloodTypeDistribution);
        Map<String, Long> districtDistribution = emergencies.stream()
                .collect(Collectors.groupingBy(EmergencyRequest::getDistrict, Collectors.counting()));
        analytics.put("districtDistribution", districtDistribution);
        Map<String, Long> hospitalDistribution = emergencies.stream()
                .collect(Collectors.groupingBy(EmergencyRequest::getHospital, Collectors.counting()));
        analytics.put("hospitalDistribution", hospitalDistribution);
        Map<String, Long> urgencyDistribution = emergencies.stream()
                .collect(Collectors.groupingBy(EmergencyRequest::getUrgencyLevel, Collectors.counting()));
        analytics.put("urgencyDistribution", urgencyDistribution);
        Map<String, Long> statusDistribution = emergencies.stream()
                .collect(Collectors.groupingBy(EmergencyRequest::getStatus, Collectors.counting()));
        analytics.put("statusDistribution", statusDistribution);
        Map<String, Long> monthlyDistribution = emergencies.stream()
                .collect(Collectors.groupingBy(
                        emergency -> emergency.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        Collectors.counting()
                ));
        analytics.put("monthlyDistribution", monthlyDistribution);
        double averageUnits = emergencies.stream()
                .mapToInt(EmergencyRequest::getUnitsNeeded)
                .average()
                .orElse(0.0);
        analytics.put("averageUnitsNeeded", Math.round(averageUnits * 100.0) / 100.0);
        analytics.put("startDate", startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        analytics.put("endDate", endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return analytics;
    }

    public Map<String, Object> generateAppointmentAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = appointmentService.getAppointmentsBetweenDates(startDate, endDate);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalAppointments", appointments.size());
        Map<String, Long> statusDistribution = appointments.stream()
                .collect(Collectors.groupingBy(
                        appointment -> appointment.getStatus().toString(),
                        Collectors.counting()
                ));
        analytics.put("statusDistribution", statusDistribution);
        Map<String, Long> hospitalDistribution = appointments.stream()
                .collect(Collectors.groupingBy(
                        appointment -> appointment.getHospital().getName(),
                        Collectors.counting()
                ));
        analytics.put("hospitalDistribution", hospitalDistribution);
        Map<String, Long> bloodTypeDistribution = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getBloodType, Collectors.counting()));
        analytics.put("bloodTypeDistribution", bloodTypeDistribution);
        Map<String, Long> monthlyDistribution = appointments.stream()
                .collect(Collectors.groupingBy(
                        appointment -> appointment.getAppointmentDateTime()
                                .format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        Collectors.counting()
                ));
        analytics.put("monthlyDistribution", monthlyDistribution);
        long confirmedAppointments = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                .count();
        double attendanceRate = appointments.isEmpty() ? 0 :
                (confirmedAppointments * 100.0) / appointments.size();
        analytics.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
        analytics.put("startDate", startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        analytics.put("endDate", endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return analytics;
    }
}