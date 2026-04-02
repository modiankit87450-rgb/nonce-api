package com.example.nonceapi.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.UUID;

@Component
public class CryptoUtil {

    static {
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    /** Generate a secure random nonce */
    public String generateRawNonce() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return UUID.randomUUID().toString().replace("-", "")
                + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** SHA-256 hash — stored in DB */
    public String hashNonce(String rawNonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");
            byte[] hash = digest.digest(rawNonce.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Nonce hash failed", e);
        }
    }

    /** Base64 encode — sent to client */
    public String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /** Base64 decode */
    public String decodeBase64(String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }
}
