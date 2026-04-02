package com.example.nonceapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NonceResponse {
    private String status;
    private String nonce;
    private String deviceId;
    private String appId;
    private String expiresAt;
    private String xRequestId;
    private String errorCode;
    private String message;
}
