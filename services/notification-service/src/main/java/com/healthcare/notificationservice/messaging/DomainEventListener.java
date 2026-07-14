package com.healthcare.notificationservice.messaging;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.healthcare.notificationservice.application.NotificationCommandService;
import com.healthcare.notificationservice.config.MessagingConfig;

@Component
public class DomainEventListener {

    private final NotificationCommandService notificationCommandService;

    public DomainEventListener(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @RabbitListener(queues = MessagingConfig.NOTIFICATION_EVENTS_QUEUE)
    public void onDomainEvent(Map<String, Object> event) {
        String eventType = String.valueOf(event.getOrDefault("eventType", "DomainEvent"));
        String referenceId = String.valueOf(event.getOrDefault("appointmentId", event.getOrDefault("invoiceId", event.getOrDefault("patientId", "unknown"))));
        String recipient = event.get("patientId") == null ? "ops@healthcare.local" : event.get("patientId") + "@healthcare.local";
        notificationCommandService.createFromDomainEvent(eventType, referenceId, recipient);
    }
}