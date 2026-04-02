package com.example.nonceapi.model;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NonceRequest {

    @NotBlank(message = "deviceId is required")
    private String id;

    @NotBlank(message = "appId is required")
    private String appId;

    @NotBlank(message = "appVer is required")
    private String appVer;

    @NotBlank(message = "cameraResolutionFront is required")
    private String cameraResolutionFront;

    @NotBlank(message = "cameraResolutionBack is required")
    private String cameraResolutionBack;

    @NotBlank(message = "cpuAbi is required")
    private String cpuAbi;

    @NotBlank(message = "deviceModel is required")
    private String deviceModel;

    @NotBlank(message = "deviceSDKVer is required")
    private String deviceSDKVer;

    @NotNull(message = "deviceScreenSize is required")
    private Integer deviceScreenSize;

    @NotBlank(message = "manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "processor is required")
    private String processor;

    @NotBlank(message = "availableRam is required")
    private String availableRam;

    @NotBlank(message = "deviceOs is required")
    private String deviceOs;

    @NotBlank(message = "deviceOsVer is required")
    private String deviceOsVer;

    @Valid
    @NotNull(message = "deviceKey is required")
    private DeviceKey deviceKey;

    @Data
    public static class DeviceKey {

        @NotBlank(message = "devicePublicKey is required")
        private String devicePublicKey;

        @NotBlank(message = "userName is required")
        private String userName;

        @NotBlank(message = "password is required")
        private String password;
    }
}