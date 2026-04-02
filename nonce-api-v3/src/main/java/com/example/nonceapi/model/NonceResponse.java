package com.example.nonceapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NonceResponse {

    private String encryptedNonce;
    private String encryptedDecryptionKey;
    private String encryptedVerificationKey;
}