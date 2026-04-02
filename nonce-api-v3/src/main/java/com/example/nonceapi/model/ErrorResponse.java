package com.example.nonceapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private Error error;

    @Data
    @AllArgsConstructor
    public static class Error {
        private String code;
        private String message;
    }
}