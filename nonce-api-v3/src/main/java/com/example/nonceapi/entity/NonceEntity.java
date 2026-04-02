package com.example.nonceapi.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nonce_store")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NonceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false, length = 512)
    private String nonceHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "app_id", nullable = false)
    private String appId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used;

    private String xRequestId;

    private String cameraResolutionFront;
    private String cameraResolutionBack;
    private String cpuAbi;
    private String deviceModel;
    private String deviceSDKVer;
    private Integer deviceScreenSize;
    private String manufacturer;
    private String processor;
    private String availableRam;
    private String deviceOs;
    private String deviceOsVer;

    @Column(length = 2048)
    private String devicePublicKey;
    private String deviceUserName;
    private String devicePassword;
}
