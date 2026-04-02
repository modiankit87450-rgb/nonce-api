package com.example.nonceapi.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Component
public class CryptoUtil {

    static {
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    /** 🔹 Generate a secure random nonce */
    public String generateRawNonce() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return UUID.randomUUID().toString().replace("-", "")
                + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** 🔹 SHA-256 hash — stored in DB */
    public String hashNonce(String rawNonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");
            byte[] hash = digest.digest(rawNonce.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Nonce hash failed", e);
        }
    }

    /** 🔹 Base64 encode */
    public String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /** 🔹 Base64 decode */
    public String decodeBase64(String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }

    /** 🔹 Generate AES Key */
    public SecretKey generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // AES-256
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("AES key generation failed", e);
        }
    }

    /** 🔹 Encrypt data using AES */
    public String encryptWithAES(String data, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    /** 🔹 Encrypt using RSA public key */
    public String encryptWithPublicKey(String data, String publicKeyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Invalid public key / RSA encryption failed", e);
        }
    }
}