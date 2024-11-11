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
            <!DOCTYPE html>
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h2 style="color: #e53935; margin: 0; font-size: 24px;">URGENT BLOOD REQUIREMENT</h2>
                        <p style="color: #555555; font-size: 16px;">A hospital in your district needs immediate assistance</p>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <div style="margin-bottom: 15px;">
                            <p style="margin: 8px 0;"><strong style="color: #333333;">Hospital:</strong> <span style="color: #555555;">%s</span></p>
                            <p style="margin: 8px 0;"><strong style="color: #333333;">Blood Type Needed:</strong> <span style="color: #e53935; font-weight: bold;">%s</span></p>
                            <p style="margin: 8px 0;"><strong style="color: #333333;">Units Required:</strong> <span style="color: #555555;">%d</span></p>
                            <p style="margin: 8px 0;"><strong style="color: #333333;">Urgency Level:</strong> <span style="color: #555555;">%s</span></p>
                            <p style="margin: 8px 0;"><strong style="color: #333333;">Contact Number:</strong> <span style="color: #555555;">%s</span></p>
                        </div>
                    </div>
                    <div style="text-align: center; margin-top: 30px;">
                        <p style="color: #666666; font-size: 15px;">Your donation can save a life. Please respond quickly.</p>
                    </div>
                </div>
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
            <!DOCTYPE html>
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h2 style="color: #1976D2; margin: 0; font-size: 24px;">Password Reset Request</h2>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <p style="text-align: center; color: #555555; margin-bottom: 20px;">Your OTP for password reset is:</p>
                        <div style="background-color: #ffffff; padding: 15px; border-radius: 4px; text-align: center; border: 1px dashed #1976D2;">
                            <h1 style="color: #1976D2; letter-spacing: 8px; margin: 0; font-size: 32px;">%s</h1>
                        </div>
                    </div>
                    <div style="text-align: center; margin-top: 30px;">
                        <p style="color: #666666; font-size: 14px; margin: 5px 0;">This OTP will expire in 5 minutes.</p>
                        <p style="color: #666666; font-size: 14px; margin: 5px 0;">If you didn't request this, please ignore this email.</p>
                    </div>
                </div>
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
            <!DOCTYPE html>
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h2 style="color: %s; margin: 0; font-size: 24px;">%s</h2>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <div style="color: #555555; font-size: 16px;">%s</div>
                    </div>
                    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee;">
                        <p style="color: #666666; font-size: 14px;">Thank you for using our blood donation service.</p>
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
            <!DOCTYPE html>
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h2 style="color: #e53935; margin: 0; font-size: 24px;">Urgent Blood Donation Needed</h2>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <p style="color: #555555; margin: 0 0 15px 0;">Dear Donor,</p>
                        <p style="color: #555555; margin: 0 0 15px 0;">We are reaching out to inform you that the blood bank in your district (%s) is running low on supplies.</p>
                        <p style="color: #555555; margin: 0 0 15px 0;">Your donation can make a significant difference. Please consider donating blood at your earliest convenience.</p>
                    </div>
                    <div style="text-align: center; margin-top: 30px;">
                        <p style="color: #666666; font-size: 15px;">Thank you for your support.</p>
                    </div>
                </div>
            </body>
            </html>
            """, district);
    }
}