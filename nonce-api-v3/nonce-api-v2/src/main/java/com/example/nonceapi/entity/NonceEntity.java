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

    @Column(nullable = false)
    private String appId;

    @Column(nullable = false, length = 512)
    private String nonceHash;   // SHA-256 hash stored, not plaintext

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used;

    private String xRequestId;
}
