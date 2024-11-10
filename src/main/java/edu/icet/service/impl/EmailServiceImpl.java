package edu.icet.service.impl;

import edu.icet.dto.DonorEntity;
import edu.icet.dto.Notification;
import edu.icet.entity.EmergencyEmailRequestEntity;
import edu.icet.entity.NotificationEntity;
import edu.icet.repository.DonorDao;
import edu.icet.repository.HospitalDao;
import edu.icet.service.EmailService;
import edu.icet.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DonorDao donorRepository;

    @Autowired
    private OTPServiceImpl otpService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HospitalDao hospitalRepository;


    public int notifyMatchingDonors(EmergencyEmailRequestEntity request) {
        try {
            List<DonorEntity> matchingDonors = donorRepository.findByBloodTypeAndDistrict(
                    request.getBloodType(),
                    request.getDistrict()
            );
            for (DonorEntity donor : matchingDonors) {
                sendEmergencyEmail(donor.getEmailAddress(), request);
            }
            return matchingDonors.size();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send notifications: " + e.getMessage());
        }
    }

    private void sendEmergencyEmail(String donorEmail, EmergencyEmailRequestEntity request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(donorEmail);
            helper.setSubject("URGENT: Blood Donation Request");
            helper.setText(createEmailContent(request), true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email to " + donorEmail);
        }
    }

    private String createEmailContent(EmergencyEmailRequestEntity request) {
        return String.format("""
            <html>
            <body>
                <h2 style="color: #ff0000;">Urgent Blood Requirement</h2>
                <p>A hospital in your district needs your help!</p>
                <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px;">
                    <p><strong>Hospital:</strong> %s</p>
                    <p><strong>Blood Type Needed:</strong> %s</p>
                    <p><strong>Units Required:</strong> %d</p>
                    <p><strong>Urgency Level:</strong> %s</p>
                    <p><strong>Contact Number:</strong> %s</p>
                </div>
                <p style="color: #666;">Your donation can save a life.</p>
            </body>
            </html>
            """,
                request.getHospitalName(),
                request.getBloodType(),
                request.getUnitsNeeded(),
                request.getUrgencyLevel(),
                request.getContactNumber()
        );
    }

    public void sendPasswordResetOTP(String email) {
        try {
            String otp = otpService.generateOTP(email);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Password Reset OTP");
            helper.setText(createOTPEmailContent(otp), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    private String createOTPEmailContent(String otp) {
        return String.format("""
            <html>
            <body>
                <h2 style="color: #2196F3;">Password Reset Request</h2>
                <p>Your OTP for password reset is:</p>
                <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; text-align: center;">
                    <h1 style="color: #1976D2; letter-spacing: 5px;">%s</h1>
                </div>
                <p style="color: #666;">This OTP will expire in 5 minutes.</p>
                <p style="color: #666;">If you didn't request this, please ignore this email.</p>
            </body>
            </html>
            """, otp);
    }

    public void sendAppointmentStatusEmail(Notification notificationDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(notificationDTO.getRecipientEmail());
            helper.setSubject(notificationDTO.getTitle());
            helper.setText(createEmailContent(notificationDTO), true);
            mailSender.send(message);
            Notification dbNotification = new Notification();
            dbNotification.setRecipientEmail(notificationDTO.getRecipientEmail());
            dbNotification.setTitle(notificationDTO.getTitle());
            dbNotification.setType(notificationDTO.getType());
            dbNotification.setMessage(createShortMessage(notificationDTO));
            notificationService.createNotification(dbNotification);

        } catch (Exception e) {
            log.error("Failed to send email or create notification", e);
            throw new RuntimeException("Failed to process notification: " + e.getMessage());
        }
    }

    private String createShortMessage(Notification notification) {
        return String.format("AppointmentEntity %s - Please check your email for details.",
                notification.getType() == NotificationEntity.NotificationType.APPOINTMENT_CONFIRMED ? "confirmed" : "cancelled");
    }

    private String createEmailContent(Notification notification) {
        String statusColor = notification.getType() == NotificationEntity.NotificationType.APPOINTMENT_CONFIRMED
                ? "#28a745" : "#dc3545";

        return String.format("""
            <html>
            <body>
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: %s;">%s</h2>
                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 15px 0;">
                        %s
                    </div>
                    <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee;">
                        <p style="color: #666; font-size: 14px;">Thank you for using our blood donation service.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                statusColor,
                notification.getTitle(),
                notification.getMessage()
        );
    }

    public void notifyDonorsForLowCapacity(String hospitalDistrict) {
        List<DonorEntity> donorsInDistrict = donorRepository.findByDistrict(hospitalDistrict);
        for (DonorEntity donor : donorsInDistrict) {
            sendLowCapacityEmail(donor.getEmailAddress(), hospitalDistrict);
        }
    }

    private void sendLowCapacityEmail(String donorEmail, String district) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(donorEmail);
            helper.setSubject("Urgent: Blood Donation Needed in Your District");
            helper.setText(createLowCapacityEmailContent(district), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send low capacity email to " + donorEmail, e);
        }
    }

    private String createLowCapacityEmailContent(String district) {
        return String.format("""
            <html>
            <body>
                <h2 style="color: #ff0000;">Urgent Blood Donation Needed</h2>
                <p>Dear Donor,</p>
                <p>We are reaching out to inform you that the blood bank in your district (%s) is running low on supplies.</p>
                <p>Your donation can make a significant difference. Please consider donating blood at your earliest convenience.</p>
                <p>Thank you for your support.</p>
            </body>
            </html>
            """, district);
    }
}