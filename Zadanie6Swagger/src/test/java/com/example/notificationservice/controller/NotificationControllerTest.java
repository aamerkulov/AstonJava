package com.example.notificationservice.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mail.javamail.JavaMailSender;
import com.example.notificationservice.config.NotificationProperties;
import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.DirectEmailRequest;
import com.example.notificationservice.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(NotificationController.class)
@ContextConfiguration(classes = {
        NotificationController.class,
        NotificationProperties.class
})
@MockBeans({
        @MockBean(EmailService.class),
        @MockBean(JavaMailSender.class)
})
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private NotificationProperties properties;
    @Test
    void shouldSendNotification() throws Exception {
        NotificationRequest request = new NotificationRequest("test@example.com", "Test message");

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(
                eq("test@example.com"),
                anyString(),
                anyString()
        );
    }
    @Test
    void shouldSendDirectEmail() throws Exception {
        DirectEmailRequest request = new DirectEmailRequest(
                "user@example.com",
                "Test Subject",
                "Test Message Content"
        );

        mockMvc.perform(post("/api/notifications/direct-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(
                eq("user@example.com"),
                eq("Test Subject"),
                eq("Test Message Content")
        );
    }
}