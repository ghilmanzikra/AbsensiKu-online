package com.example.absenku.models;

import java.util.List;

public class UpdateProfileResponse {
    private String message;
    private List<String> updated_fields;

    public UpdateProfileResponse() {
    }

    public UpdateProfileResponse(String message, List<String> updated_fields) {
        this.message = message;
        this.updated_fields = updated_fields;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getUpdated_fields() {
        return updated_fields;
    }

    public void setUpdated_fields(List<String> updated_fields) {
        this.updated_fields = updated_fields;
    }
}
