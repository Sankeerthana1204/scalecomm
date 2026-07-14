package com.healthcare.billingservice.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthcare.billingservice.api.InvoiceResponse;
import com.healthcare.billingservice.domain.InvoiceRepository;

@Service
public class InvoiceQueryService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceQueryService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(InvoiceMapper::toResponse)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> search(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return invoiceRepository.findAll().stream().map(InvoiceMapper::toResponse).toList();
        }
        return invoiceRepository.findByPatientId(patientId).stream().map(InvoiceMapper::toResponse).toList();
    }
}
