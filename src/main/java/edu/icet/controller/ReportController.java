package edu.icet.controller;

import edu.icet.service.PdfGeneratorService;
import edu.icet.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin
public class ReportController {
    private final ReportService reportService;
    private final PdfGeneratorService pdfGeneratorService;

    @GetMapping("/district/{district}")
    public ResponseEntity<?> getDistrictReport(@PathVariable String district) {
        try {
            Map<String, Object> reportData = reportService.generateDonorAnalyticsByDistrict(district);
            byte[] pdfBytes = pdfGeneratorService.generateDonorReport(reportData, district);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=donor-report-" + district + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/hospitals/{district}")
    public ResponseEntity<?> getHospitalReport(@PathVariable String district) {
        try {
            Map<String, Object> reportData = reportService.generateHospitalAnalyticsByDistrict(district);
            byte[] pdfBytes = pdfGeneratorService.generateHospitalReport(reportData, district);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hospital-report-" + district + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/campaigns")
    public ResponseEntity<?> getCampaignReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            Map<String, Object> reportData = reportService.generateCampaignAnalytics(startDate, endDate);
            byte[] pdfBytes = pdfGeneratorService.generateCampaignReport(reportData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=campaign-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/emergencies")
    public ResponseEntity<?> getEmergencyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            Map<String, Object> reportData = reportService.generateEmergencyAnalytics(startDate, endDate);
            byte[] pdfBytes = pdfGeneratorService.generateEmergencyReport(reportData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=emergency-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointmentReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            Map<String, Object> reportData = reportService.generateAppointmentAnalytics(startDate, endDate);
            byte[] pdfBytes = pdfGeneratorService.generateAppointmentReport(reportData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=appointment-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}