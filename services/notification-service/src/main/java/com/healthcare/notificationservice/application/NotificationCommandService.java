package com.healthcare.notificationservice.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthcare.notificationservice.api.NotificationRequest;
import com.healthcare.notificationservice.api.NotificationResponse;
import com.healthcare.notificationservice.domain.Notification;
import com.healthcare.notificationservice.domain.NotificationRepository;

@Service
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;

    public NotificationCommandService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        return NotificationMapper.toResponse(notificationRepository.save(NotificationMapper.newEntity(request)));
    }

    @Transactional
    public void createFromDomainEvent(String eventType, String referenceId, String recipient) {
        Notification notification = new Notification();
        notification.setChannel("EMAIL");
        notification.setRecipient(recipient == null || recipient.isBlank() ? "ops@healthcare.local" : recipient);
        notification.setSubject(eventType + " processed");
        notification.setBody("Event " + eventType + " was received for reference " + referenceId);
        notification.setStatus("QUEUED");
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
