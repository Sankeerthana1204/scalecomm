package com.healthcare.billingservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.healthcare.billingservice.application.InvoiceCommandService;
import com.healthcare.billingservice.config.MessagingConfig;
import com.healthcare.billingservice.messaging.dto.AppointmentBookedEvent;

@Component
public class AppointmentEventListener {

    private final InvoiceCommandService invoiceCommandService;

    public AppointmentEventListener(InvoiceCommandService invoiceCommandService) {
        this.invoiceCommandService = invoiceCommandService;
    }

    @RabbitListener(queues = MessagingConfig.APPOINTMENT_EVENTS_QUEUE)
    public void onAppointmentBooked(AppointmentBookedEvent event) {
        invoiceCommandService.createFromAppointment(event);
    }
}