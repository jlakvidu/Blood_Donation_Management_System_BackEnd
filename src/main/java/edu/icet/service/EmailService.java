package edu.icet.service;

import edu.icet.dto.Notification;
import edu.icet.entity.EmergencyEmailRequestEntity;

public interface EmailService {
    int notifyMatchingDonors(EmergencyEmailRequestEntity request);
    void sendPasswordResetOTP(String email);
    void sendAppointmentStatusEmail(Notification notificationDTO);
    void notifyDonorsForLowCapacity(String hospitalDistrict);
}

