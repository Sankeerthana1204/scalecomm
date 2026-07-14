package com.healthcare.billingservice.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthcare.billingservice.api.InvoiceRequest;
import com.healthcare.billingservice.api.InvoiceResponse;
import com.healthcare.billingservice.domain.Invoice;
import com.healthcare.billingservice.domain.InvoiceRepository;
import com.healthcare.billingservice.messaging.InvoiceEventPublisher;
import com.healthcare.billingservice.messaging.dto.AppointmentBookedEvent;

@Service
public class InvoiceCommandService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceEventPublisher invoiceEventPublisher;

    public InvoiceCommandService(InvoiceRepository invoiceRepository, InvoiceEventPublisher invoiceEventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceEventPublisher = invoiceEventPublisher;
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Invoice invoice = invoiceRepository.save(InvoiceMapper.newEntity(request));
        invoiceEventPublisher.publishIssued(invoice);
        return InvoiceMapper.toResponse(invoice);
    }

    @Transactional
    public void createFromAppointment(AppointmentBookedEvent event) {
        Invoice invoice = new Invoice();
        invoice.setPatientId(event.patientId());
        invoice.setAppointmentId(event.appointmentId());
        invoice.setAmount(new BigDecimal("500.00"));
        invoice.setCurrency("INR");
        invoice.setStatus("ISSUED");
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setDueAt(LocalDateTime.now().plusDays(7));
        Invoice saved = invoiceRepository.save(invoice);
        invoiceEventPublisher.publishIssued(saved);
    }
}
