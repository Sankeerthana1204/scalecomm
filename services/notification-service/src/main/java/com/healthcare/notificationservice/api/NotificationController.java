package com.healthcare.notificationservice.api;

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

import com.healthcare.notificationservice.application.NotificationCommandService;
import com.healthcare.notificationservice.application.NotificationQueryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationCommandService notificationCommandService, NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationCommandService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/notifications/" + response.notificationId())).body(response);
    }

    @GetMapping("/{notificationId}")
    public NotificationResponse getById(@PathVariable String notificationId) {
        return notificationQueryService.getById(notificationId);
    }

    @GetMapping
    public List<NotificationResponse> search(@RequestParam(required = false) String recipient) {
        return notificationQueryService.search(recipient);
    }
}