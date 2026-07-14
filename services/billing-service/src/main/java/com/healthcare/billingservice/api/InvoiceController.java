package com.healthcare.billingservice.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.billingservice.application.InvoiceCommandService;
import com.healthcare.billingservice.application.InvoiceQueryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices")
public class InvoiceController {

    private final InvoiceCommandService invoiceCommandService;
    private final InvoiceQueryService invoiceQueryService;

    public InvoiceController(InvoiceCommandService invoiceCommandService, InvoiceQueryService invoiceQueryService) {
        this.invoiceCommandService = invoiceCommandService;
        this.invoiceQueryService = invoiceQueryService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceCommandService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/invoices/" + response.invoiceId())).body(response);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceResponse getById(@PathVariable String invoiceId) {
        return invoiceQueryService.getById(invoiceId);
    }

    @GetMapping
    public List<InvoiceResponse> search(@RequestParam(required = false) String patientId) {
        return invoiceQueryService.search(patientId);
    }
}