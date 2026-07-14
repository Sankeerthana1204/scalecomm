package com.healthcare.notificationservice.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthcare.notificationservice.api.NotificationResponse;
import com.healthcare.notificationservice.domain.NotificationRepository;

@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(String notificationId) {
        return notificationRepository.findById(notificationId)
                .map(NotificationMapper::toResponse)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> search(String recipient) {
        if (recipient == null || recipient.isBlank()) {
            return notificationRepository.findAll().stream().map(NotificationMapper::toResponse).toList();
        }
        return notificationRepository.findByRecipientContainingIgnoreCase(recipient)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
}
