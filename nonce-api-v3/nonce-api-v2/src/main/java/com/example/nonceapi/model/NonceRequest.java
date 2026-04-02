package com.example.nonceapi.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class NonceRequest {
    @NotBlank(message = "deviceId is required")
    private String deviceId;

    @NotBlank(message = "appId is required")
    private String appId;

    // Optional fields
    private String deviceMake;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
}
