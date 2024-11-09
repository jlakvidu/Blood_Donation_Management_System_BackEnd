package edu.icet.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static edu.icet.service.PdfGeneratorService.PRIMARY_COLOR;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {
    public static final BaseColor PRIMARY_COLOR = new BaseColor(229, 30, 99); // #e51e63
    public static final BaseColor SECONDARY_COLOR = new BaseColor(33, 150, 243); // #2196f3
    public static final BaseColor TEXT_GRAY = new BaseColor(102, 102, 102);

    public byte[] generateDonorReport(Map<String, Object> reportData, String district) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36); // Larger margins
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setPageEvent(new HeaderFooterPageEvent());

        document.open();

        // Add logo
        //Image logo = Image.getInstance(new ClassPathResource("static/images/blood-drop-logo.png").getInputStream());
        //logo.scaleToFit(60, 60);
        //logo.setAlignment(Element.ALIGN_CENTER);
        //document.add(logo);

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
        Paragraph title = new Paragraph("Donor Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, SECONDARY_COLOR);
        Paragraph subtitle = new Paragraph(district + " District", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);
        addSummarySection(document, reportData);
        addBloodTypeDistribution(document, reportData);
        addAgeDemographics(document, reportData);
        document.close();
        return outputStream.toByteArray();
    }

    private void addSummarySection(Document document, Map<String, Object> reportData) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(1);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(20);
        summaryTable.setSpacingAfter(30);

        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        PdfPCell summaryCell = new PdfPCell(new Phrase("Total Donors: " + reportData.get("totalDonors"), summaryFont));
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setBackgroundColor(new BaseColor(252, 228, 236));
        summaryCell.setPadding(15);
        summaryTable.addCell(summaryCell);

        document.add(summaryTable);
    }

    private void addBloodTypeDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph bloodTypeTitle = new Paragraph("Blood Type Distribution", sectionTitleFont);
        bloodTypeTitle.setSpacingBefore(20);
        document.add(bloodTypeTitle);

        PdfPTable bloodTypeTable = new PdfPTable(2);
        bloodTypeTable.setWidthPercentage(100);
        bloodTypeTable.setSpacingBefore(10);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Blood Type", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Count", headerFont));
        headerCell1.setBackgroundColor(SECONDARY_COLOR);
        headerCell2.setBackgroundColor(SECONDARY_COLOR);
        styleTableCell(headerCell1);
        styleTableCell(headerCell2);
        bloodTypeTable.addCell(headerCell1);
        bloodTypeTable.addCell(headerCell2);

        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);
        Map<String, Long> bloodTypeDistribution = (Map<String, Long>) reportData.get("bloodTypeDistribution");
        bloodTypeDistribution.forEach((type, count) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(type, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(count.toString(), dataFont));
            styleTableCell(cell1);
            styleTableCell(cell2);
            bloodTypeTable.addCell(cell1);
            bloodTypeTable.addCell(cell2);
        });

        document.add(bloodTypeTable);
    }

    private void addAgeDemographics(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph ageTitle = new Paragraph("Age Demographics", sectionTitleFont);
        ageTitle.setSpacingBefore(30);
        document.add(ageTitle);

        PdfPTable ageTable = new PdfPTable(2);
        ageTable.setWidthPercentage(100);
        ageTable.setSpacingBefore(10);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Age Group", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Count", headerFont));
        headerCell1.setBackgroundColor(SECONDARY_COLOR);
        headerCell2.setBackgroundColor(SECONDARY_COLOR);
        styleTableCell(headerCell1);
        styleTableCell(headerCell2);
        ageTable.addCell(headerCell1);
        ageTable.addCell(headerCell2);

        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);
        Map<String, Long> ageDemographics = (Map<String, Long>) reportData.get("ageDemographics");
        ageDemographics.forEach((ageGroup, count) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(ageGroup, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(count.toString(), dataFont));
            styleTableCell(cell1);
            styleTableCell(cell2);
            ageTable.addCell(cell1);
            ageTable.addCell(cell2);
        });

        document.add(ageTable);
    }

    private void styleTableCell(PdfPCell cell) {
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    public byte[] generateHospitalReport(Map<String, Object> reportData, String district) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
        Paragraph title = new Paragraph("Hospital Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, SECONDARY_COLOR);
        Paragraph subtitle = new Paragraph(district + " District", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);

        addHospitalSummarySection(document, reportData);

        addHospitalTypeDistribution(document, reportData);

        addCapacityAnalysis(document, reportData);
        document.close();
        return outputStream.toByteArray();
    }

    private void addHospitalSummarySection(Document document, Map<String, Object> reportData) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(1);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(20);
        summaryTable.setSpacingAfter(30);

        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        PdfPCell summaryCell = new PdfPCell(new Phrase("Total Hospitals: " + reportData.get("totalHospitals"), summaryFont));
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setBackgroundColor(new BaseColor(252, 228, 236));
        summaryCell.setPadding(15);
        summaryTable.addCell(summaryCell);

        document.add(summaryTable);
    }

    private void addHospitalTypeDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph typeTitle = new Paragraph("Hospital Type Distribution", sectionTitleFont);
        typeTitle.setSpacingBefore(20);
        document.add(typeTitle);

        PdfPTable typeTable = new PdfPTable(2);
        typeTable.setWidthPercentage(100);
        typeTable.setSpacingBefore(10);

        addTableHeaders(typeTable, "Hospital Type", "Count");
        Map<String, Long> typeDistribution = (Map<String, Long>) reportData.get("hospitalTypeDistribution");
        addTableData(typeTable, typeDistribution);

        document.add(typeTable);
    }

    private void addCapacityAnalysis(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph capacityTitle = new Paragraph("Blood Bank Capacity Analysis", sectionTitleFont);
        capacityTitle.setSpacingBefore(30);
        document.add(capacityTitle);

        PdfPTable capacityTable = new PdfPTable(2);
        capacityTable.setWidthPercentage(100);
        capacityTable.setSpacingBefore(10);

        addTableHeaders(capacityTable, "Capacity Range", "Number of Hospitals");
        Map<String, Long> capacityRanges = (Map<String, Long>) reportData.get("capacityRanges");
        addTableData(capacityTable, capacityRanges);

        document.add(capacityTable);

        Font averageFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);
        Paragraph averageCapacity = new Paragraph(
                "Average Blood Bank Capacity: " + reportData.get("averageCapacity") + " units",
                averageFont
        );
        averageCapacity.setSpacingBefore(20);
        document.add(averageCapacity);
    }

    private void addTableHeaders(PdfPTable table, String header1, String header2) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

        PdfPCell headerCell1 = new PdfPCell(new Phrase(header1, headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase(header2, headerFont));

        headerCell1.setBackgroundColor(SECONDARY_COLOR);
        headerCell2.setBackgroundColor(SECONDARY_COLOR);

        styleTableCell(headerCell1);
        styleTableCell(headerCell2);

        table.addCell(headerCell1);
        table.addCell(headerCell2);
    }

    private void addTableData(PdfPTable table, Map<String, Long> data) {
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);

        data.forEach((key, value) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(key, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(value.toString(), dataFont));

            styleTableCell(cell1);
            styleTableCell(cell2);

            table.addCell(cell1);
            table.addCell(cell2);
        });
    }

    public byte[] generateCampaignReport(Map<String, Object> reportData) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
        Paragraph title = new Paragraph("Campaign Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, SECONDARY_COLOR);
        String dateRange = reportData.get("startDate") + " - " + reportData.get("endDate");
        Paragraph subtitle = new Paragraph(dateRange, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);
        addCampaignSummarySection(document, reportData);
        addDistrictDistribution(document, reportData);
        addHospitalDistribution(document, reportData);
        addMonthlyDistribution(document, reportData);
        document.close();
        return outputStream.toByteArray();
    }

    private void addCampaignSummarySection(Document document, Map<String, Object> reportData) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(1);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(20);
        summaryTable.setSpacingAfter(30);

        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        PdfPCell summaryCell = new PdfPCell(new Phrase(
                "Total Campaigns: " + reportData.get("totalCampaigns"),
                summaryFont
        ));
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setBackgroundColor(new BaseColor(252, 228, 236));
        summaryCell.setPadding(15);
        summaryTable.addCell(summaryCell);

        document.add(summaryTable);
    }

    private void addDistrictDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, PdfGeneratorService.SECONDARY_COLOR);
        Paragraph districtTitle = new Paragraph("Campaign Distribution by District", sectionTitleFont);
        districtTitle.setSpacingBefore(20);
        document.add(districtTitle);

        PdfPTable districtTable = new PdfPTable(2);
        districtTable.setWidthPercentage(100);
        districtTable.setSpacingBefore(10);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("District", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Number of Campaigns", headerFont));

        headerCell1.setBackgroundColor(PdfGeneratorService.SECONDARY_COLOR);
        headerCell2.setBackgroundColor(PdfGeneratorService.SECONDARY_COLOR);

        headerCell1.setPadding(10);
        headerCell2.setPadding(10);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        districtTable.addCell(headerCell1);
        districtTable.addCell(headerCell2);

        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, PdfGeneratorService.TEXT_GRAY);
        Map<String, Long> districtDistribution = (Map<String, Long>) reportData.get("districtDistribution");
        districtDistribution.forEach((key, value) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(key, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(value.toString(), dataFont));

            cell1.setPadding(10);
            cell2.setPadding(10);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            districtTable.addCell(cell1);
            districtTable.addCell(cell2);
        });

        document.add(districtTable);
    }

    private void addHospitalDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, PdfGeneratorService.SECONDARY_COLOR);
        Paragraph hospitalTitle = new Paragraph("Campaign Distribution by Hospital", sectionTitleFont);
        hospitalTitle.setSpacingBefore(30);
        document.add(hospitalTitle);

        PdfPTable hospitalTable = new PdfPTable(2);
        hospitalTable.setWidthPercentage(100);
        hospitalTable.setSpacingBefore(10);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Hospital", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Number of Campaigns", headerFont));

        headerCell1.setBackgroundColor(SECONDARY_COLOR);
        headerCell2.setBackgroundColor(SECONDARY_COLOR);

        headerCell1.setPadding(10);
        headerCell2.setPadding(10);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        hospitalTable.addCell(headerCell1);
        hospitalTable.addCell(headerCell2);

        Map<String, Long> hospitalDistribution = (Map<String, Long>) reportData.get("hospitalDistribution");
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);
        hospitalDistribution.forEach((key, value) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(key, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(value.toString(), dataFont));

            cell1.setPadding(10);
            cell2.setPadding(10);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            hospitalTable.addCell(cell1);
            hospitalTable.addCell(cell2);
        });

        document.add(hospitalTable);
    }

    private void addMonthlyDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph monthlyTitle = new Paragraph("Monthly Campaign Distribution", sectionTitleFont);
        monthlyTitle.setSpacingBefore(30);
        document.add(monthlyTitle);

        PdfPTable monthlyTable = new PdfPTable(2);
        monthlyTable.setWidthPercentage(100);
        monthlyTable.setSpacingBefore(10);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Month", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Number of Campaigns", headerFont));

        headerCell1.setBackgroundColor(SECONDARY_COLOR);
        headerCell2.setBackgroundColor(SECONDARY_COLOR);

        headerCell1.setPadding(10);
        headerCell2.setPadding(10);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        monthlyTable.addCell(headerCell1);
        monthlyTable.addCell(headerCell2);

        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);
        Map<String, Long> monthlyDistribution = (Map<String, Long>) reportData.get("monthlyDistribution");

        monthlyDistribution.forEach((key, value) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(key, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(value.toString(), dataFont));

            cell1.setPadding(10);
            cell2.setPadding(10);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            monthlyTable.addCell(cell1);
            monthlyTable.addCell(cell2);
        });

        document.add(monthlyTable);
    }

    public byte[] generateEmergencyReport(Map<String, Object> reportData) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
        Paragraph title = new Paragraph("Emergency Requests Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, SECONDARY_COLOR);
        String dateRange = reportData.get("startDate") + " - " + reportData.get("endDate");
        Paragraph subtitle = new Paragraph(dateRange, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);

        addEmergencySummarySection(document, reportData);
        addBloodTypeDistributionForEmergencies(document, reportData);
        addUrgencyDistribution(document, reportData);
        addStatusDistribution(document, reportData);
        addDistrictDistributionForEmergencies(document, reportData);
        addHospitalDistributionForEmergencies(document, reportData);
        addMonthlyDistributionForEmergencies(document, reportData);
        document.close();
        return outputStream.toByteArray();
    }

    private void addEmergencySummarySection(Document document, Map<String, Object> reportData) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(1);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(20);
        summaryTable.setSpacingAfter(30);

        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        String summaryText = String.format("Total Emergency Requests: %s\nAverage Units Needed: %s",
                reportData.get("totalEmergencies"),
                reportData.get("averageUnitsNeeded"));

        PdfPCell summaryCell = new PdfPCell(new Phrase(summaryText, summaryFont));
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setBackgroundColor(new BaseColor(252, 228, 236));
        summaryCell.setPadding(15);
        summaryTable.addCell(summaryCell);

        document.add(summaryTable);
    }

    private void addBloodTypeDistributionForEmergencies(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph bloodTypeTitle = new Paragraph("Blood Type Distribution", sectionTitleFont);
        bloodTypeTitle.setSpacingBefore(20);
        document.add(bloodTypeTitle);

        PdfPTable bloodTypeTable = new PdfPTable(2);
        bloodTypeTable.setWidthPercentage(100);
        bloodTypeTable.setSpacingBefore(10);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Blood Type", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Count", headerFont));
        headerCell1.setBackgroundColor(SECONDARY_COLOR);
        headerCell2.setBackgroundColor(SECONDARY_COLOR);
        styleTableCell(headerCell1);
        styleTableCell(headerCell2);
        bloodTypeTable.addCell(headerCell1);
        bloodTypeTable.addCell(headerCell2);
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, TEXT_GRAY);
        Map<String, Long> bloodTypeDistribution = (Map<String, Long>) reportData.get("bloodTypeDistribution");
        bloodTypeDistribution.forEach((type, count) -> {
            PdfPCell cell1 = new PdfPCell(new Phrase(type, dataFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(count.toString(), dataFont));
            styleTableCell(cell1);
            styleTableCell(cell2);
            bloodTypeTable.addCell(cell1);
            bloodTypeTable.addCell(cell2);
        });

        document.add(bloodTypeTable);
    }

    private void addUrgencyDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph urgencyTitle = new Paragraph("Urgency Level Distribution", sectionTitleFont);
        urgencyTitle.setSpacingBefore(30);
        document.add(urgencyTitle);

        PdfPTable urgencyTable = new PdfPTable(2);
        urgencyTable.setWidthPercentage(100);
        urgencyTable.setSpacingBefore(10);
        addTableHeaders(urgencyTable, "Urgency Level", "Count");
        Map<String, Long> urgencyDistribution = (Map<String, Long>) reportData.get("urgencyDistribution");
        addTableData(urgencyTable, urgencyDistribution);

        document.add(urgencyTable);
    }

    private void addStatusDistribution(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph statusTitle = new Paragraph("Status Distribution", sectionTitleFont);
        statusTitle.setSpacingBefore(30);
        document.add(statusTitle);

        PdfPTable statusTable = new PdfPTable(2);
        statusTable.setWidthPercentage(100);
        statusTable.setSpacingBefore(10);

        addTableHeaders(statusTable, "Status", "Count");
        Map<String, Long> statusDistribution = (Map<String, Long>) reportData.get("statusDistribution");
        addTableData(statusTable, statusDistribution);

        document.add(statusTable);
    }

    private void addDistrictDistributionForEmergencies(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph districtTitle = new Paragraph("District Distribution", sectionTitleFont);
        districtTitle.setSpacingBefore(30);
        document.add(districtTitle);

        PdfPTable districtTable = new PdfPTable(2);
        districtTable.setWidthPercentage(100);
        districtTable.setSpacingBefore(10);

        addTableHeaders(districtTable, "District", "Count");
        Map<String, Long> districtDistribution = (Map<String, Long>) reportData.get("districtDistribution");
        addTableData(districtTable, districtDistribution);

        document.add(districtTable);
    }

    private void addHospitalDistributionForEmergencies(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph hospitalTitle = new Paragraph("Hospital Distribution", sectionTitleFont);
        hospitalTitle.setSpacingBefore(30);
        document.add(hospitalTitle);

        PdfPTable hospitalTable = new PdfPTable(2);
        hospitalTable.setWidthPercentage(100);
        hospitalTable.setSpacingBefore(10);

        addTableHeaders(hospitalTable, "Hospital", "Count");
        Map<String, Long> hospitalDistribution = (Map<String, Long>) reportData.get("hospitalDistribution");
        addTableData(hospitalTable, hospitalDistribution);

        document.add(hospitalTable);
    }

    private void addMonthlyDistributionForEmergencies(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph monthlyTitle = new Paragraph("Monthly Distribution", sectionTitleFont);
        monthlyTitle.setSpacingBefore(30);
        document.add(monthlyTitle);

        PdfPTable monthlyTable = new PdfPTable(2);
        monthlyTable.setWidthPercentage(100);
        monthlyTable.setSpacingBefore(10);

        addTableHeaders(monthlyTable, "Month", "Count");
        Map<String, Long> monthlyDistribution = (Map<String, Long>) reportData.get("monthlyDistribution");
        addTableData(monthlyTable, monthlyDistribution);

        document.add(monthlyTable);
    }

    public byte[] generateAppointmentReport(Map<String, Object> reportData) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY_COLOR);
        Paragraph title = new Paragraph("Appointment Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, SECONDARY_COLOR);
        String dateRange = reportData.get("startDate") + " - " + reportData.get("endDate");
        Paragraph subtitle = new Paragraph(dateRange, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);
        addAppointmentSummarySection(document, reportData);
        addStatusDistributionForAppointments(document, reportData);
        addBloodTypeDistributionForAppointments(document, reportData);
        addHospitalDistributionForAppointments(document, reportData);
        addMonthlyDistributionForAppointments(document, reportData);
        document.close();
        return outputStream.toByteArray();
    }

    private void addAppointmentSummarySection(Document document, Map<String, Object> reportData) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(1);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(20);
        summaryTable.setSpacingAfter(30);

        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        String summaryText = String.format("Total Appointments: %s\nAttendance Rate: %.1f%%",
                reportData.get("totalAppointments"),
                reportData.get("attendanceRate"));

        PdfPCell summaryCell = new PdfPCell(new Phrase(summaryText, summaryFont));
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setBackgroundColor(new BaseColor(252, 228, 236));
        summaryCell.setPadding(15);
        summaryTable.addCell(summaryCell);

        document.add(summaryTable);
    }

    private void addStatusDistributionForAppointments(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph statusTitle = new Paragraph("Status Distribution", sectionTitleFont);
        statusTitle.setSpacingBefore(30);
        document.add(statusTitle);

        PdfPTable statusTable = new PdfPTable(2);
        statusTable.setWidthPercentage(100);
        statusTable.setSpacingBefore(10);

        addTableHeaders(statusTable, "Status", "Count");
        Map<String, Long> statusDistribution = (Map<String, Long>) reportData.get("statusDistribution");
        addTableData(statusTable, statusDistribution);

        document.add(statusTable);
    }

    private void addBloodTypeDistributionForAppointments(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph bloodTypeTitle = new Paragraph("Blood Type Distribution", sectionTitleFont);
        bloodTypeTitle.setSpacingBefore(30);
        document.add(bloodTypeTitle);

        PdfPTable bloodTypeTable = new PdfPTable(2);
        bloodTypeTable.setWidthPercentage(100);
        bloodTypeTable.setSpacingBefore(10);

        addTableHeaders(bloodTypeTable, "Blood Type", "Count");
        Map<String, Long> bloodTypeDistribution = (Map<String, Long>) reportData.get("bloodTypeDistribution");
        addTableData(bloodTypeTable, bloodTypeDistribution);

        document.add(bloodTypeTable);
    }

    private void addHospitalDistributionForAppointments(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph hospitalTitle = new Paragraph("Hospital Distribution", sectionTitleFont);
        hospitalTitle.setSpacingBefore(30);
        document.add(hospitalTitle);

        PdfPTable hospitalTable = new PdfPTable(2);
        hospitalTable.setWidthPercentage(100);
        hospitalTable.setSpacingBefore(10);

        addTableHeaders(hospitalTable, "Hospital", "Count");
        Map<String, Long> hospitalDistribution = (Map<String, Long>) reportData.get("hospitalDistribution");
        addTableData(hospitalTable, hospitalDistribution);

        document.add(hospitalTable);
    }

    private void addMonthlyDistributionForAppointments(Document document, Map<String, Object> reportData) throws DocumentException {
        Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, SECONDARY_COLOR);
        Paragraph monthlyTitle = new Paragraph("Monthly Distribution", sectionTitleFont);
        monthlyTitle.setSpacingBefore(30);
        document.add(monthlyTitle);

        PdfPTable monthlyTable = new PdfPTable(2);
        monthlyTable.setWidthPercentage(100);
        monthlyTable.setSpacingBefore(10);

        addTableHeaders(monthlyTable, "Month", "Count");
        Map<String, Long> monthlyDistribution = (Map<String, Long>) reportData.get("monthlyDistribution");
        addTableData(monthlyTable, monthlyDistribution);

        document.add(monthlyTable);
    }
}
class HeaderFooterPageEvent extends PdfPageEventHelper {
    private PdfTemplate template;
    private Image total;

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        template = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(template);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            cb.setColorFill(PRIMARY_COLOR);
            cb.rectangle(document.left(), document.top() + 30, document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(), 2);
            cb.fill();
            cb.restoreState();

            cb.saveState();
            cb.setColorFill(PdfGeneratorService.TEXT_GRAY);
            cb.beginText();
            cb.setFontAndSize(BaseFont.createFont(), 8);
            cb.showTextAligned(Element.ALIGN_CENTER,
                    "Page " + writer.getPageNumber() + " of ",
                    (document.right() + document.left())/2 - 20,
                    document.bottom() - 20, 0);
            cb.endText();
            cb.addTemplate(template, (document.right() + document.left())/2 + 20, document.bottom() - 20);
            cb.restoreState();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        template.beginText();
        try {
            template.setFontAndSize(BaseFont.createFont(), 8);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        template.showText(String.valueOf(writer.getPageNumber()));
        template.endText();
    }
}