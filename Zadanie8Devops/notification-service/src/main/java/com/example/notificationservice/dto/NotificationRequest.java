package com.example.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationRequest {
    private final String email;
    private final String message;

    @JsonCreator
    public NotificationRequest(
            @JsonProperty("email") String email,
            @JsonProperty("message") String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}