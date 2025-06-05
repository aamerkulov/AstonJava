package com.example.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectEmailRequest {
    private String toEmail;
    private String subject;
    private String message;

    public String getToEmail() {
        return toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}