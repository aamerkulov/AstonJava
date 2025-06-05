package com.example.userservice.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserEvent implements Serializable{
    private final String email;
    private final String operation;
    private final String message;

    @JsonCreator
    public UserEvent(
            @JsonProperty("email") String email,
            @JsonProperty("operation") String operation,
            @JsonProperty("message") String message) {
        this.email = email;
        this.operation = operation;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getOperation() {
        return operation;
    }

    public String getMessage() {
        return message;
    }
}