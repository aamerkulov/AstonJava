package com.example.notificationservice.kafka;

import com.example.notificationservice.config.NotificationProperties;
import com.example.notificationservice.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final JavaMailSender mailSender;
    private final NotificationProperties properties;

    public UserEventConsumer(KafkaTemplate<String, UserEvent> kafkaTemplate,
                             JavaMailSender mailSender,
                             NotificationProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @KafkaListener(topics = "user-events")
    public void handleUserEvent(UserEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getEmailFrom());
        message.setTo(event.getEmail());
        message.setSubject("Notification");
        message.setText("Event: " + event.getOperation());

        mailSender.send(message);
    }
}