package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.DirectEmailRequest;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.config.NotificationProperties;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {
    private final EmailService emailService;
    private final NotificationProperties properties;
    private final JavaMailSender mailSender;

    public NotificationController(EmailService emailService,
                                  NotificationProperties properties, JavaMailSender mailSender) {
        this.emailService = emailService;
        this.properties = properties;
        this.mailSender = mailSender;
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        String subject = "Уведомление от сайта";
        String text = String.format("Здравствуйте!\n%s\nС уважением, команда %s",
                request.getMessage(), properties.getSiteUrl());

        emailService.sendEmail(request.getEmail(), subject, text);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/direct-email")
    public ResponseEntity<Void> sendDirectEmail(@RequestBody DirectEmailRequest request) {
        log.info("Received direct email request to: {}", request.getToEmail());
        emailService.sendEmail(
                request.getToEmail(),
                request.getSubject(),
                request.getMessage()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Object> testEndpoint() {
        Object response = new Object() {
            public final String message = "Service is working";
            public final Link selfLink = WebMvcLinkBuilder.linkTo(
                    methodOn(NotificationController.class).testEndpoint()).withSelfRel();
            public final Link emailLink = WebMvcLinkBuilder.linkTo(
                    methodOn(NotificationController.class).testEmail()).withRel("test-email");
        };
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-email")
    public ResponseEntity<Object> testEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("aam10012002@gmail.com");
            message.setSubject("Test Subject");
            message.setText("Test Content");
            mailSender.send(message);

            Object response = new Object() {
                public final String message = "Email sent";
                public final Link selfLink = WebMvcLinkBuilder.linkTo(
                        methodOn(NotificationController.class).testEmail()).withSelfRel();
                public final Link serviceLink = WebMvcLinkBuilder.linkTo(
                        methodOn(NotificationController.class).testEndpoint()).withRel("service-status");
            };
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error: " + e.getMessage());
        }
    }
}