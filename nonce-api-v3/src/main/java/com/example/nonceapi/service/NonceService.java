package com.example.nonceapi.service;

import com.example.nonceapi.entity.NonceEntity;
import com.example.nonceapi.model.NonceRequest;
import com.example.nonceapi.model.NonceResponse;
import com.example.nonceapi.repository.NonceRepository;
import com.example.nonceapi.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class NonceService {

    private static final Logger log = LoggerFactory.getLogger(NonceService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final NonceRepository nonceRepo;
    private final CryptoUtil cryptoUtil;

    @Value("${app.nonce.expiry.minutes:10}")
    private int nonceExpiryMinutes;

    public NonceService(NonceRepository nonceRepo, CryptoUtil cryptoUtil) {
        this.nonceRepo = nonceRepo;
        this.cryptoUtil = cryptoUtil;
    }

    @Transactional
    public NonceResponse generateNonce(NonceRequest request, String xRequestId) {

        String rawNonce  = cryptoUtil.generateRawNonce();
        String nonceHash = cryptoUtil.hashNonce(rawNonce);

        LocalDateTime now       = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(nonceExpiryMinutes);

        NonceEntity.NonceEntityBuilder builder = NonceEntity.builder()
                .deviceId(request.getId())
                .appId(request.getAppId())
                .nonceHash(nonceHash)
                .createdAt(now)
                .expiresAt(expiresAt)
                .used(false)
                .xRequestId(xRequestId)
                .cameraResolutionFront(request.getCameraResolutionFront())
                .cameraResolutionBack(request.getCameraResolutionBack())
                .cpuAbi(request.getCpuAbi())
                .deviceModel(request.getDeviceModel())
                .deviceSDKVer(request.getDeviceSDKVer())
                .deviceScreenSize(request.getDeviceScreenSize())
                .manufacturer(request.getManufacturer())
                .processor(request.getProcessor())
                .availableRam(request.getAvailableRam())
                .deviceOs(request.getDeviceOs())
                .deviceOsVer(request.getDeviceOsVer());


        if (request.getDeviceKey() != null) {
            builder.devicePublicKey(request.getDeviceKey().getDevicePublicKey())
                    .deviceUserName(request.getDeviceKey().getUserName())
                    .devicePassword(request.getDeviceKey().getPassword());
        }

        nonceRepo.save(builder.build());

        log.info("Nonce generated | deviceId={} xRequestId={}", request.getId(), xRequestId);

        try {
            // AES key
            var aesKey = cryptoUtil.generateAESKey();

            // Encrypt nonce
            String encryptedNonce = cryptoUtil.encryptWithAES(rawNonce, aesKey);

            // Encrypt AES key with public key
            String encryptedDecryptionKey = cryptoUtil.encryptWithPublicKey(
                    Base64.getEncoder().encodeToString(aesKey.getEncoded()),
                    request.getDeviceKey().getDevicePublicKey()
            );

            // Verification key
            String encryptedVerificationKey = cryptoUtil.encryptWithPublicKey(
                    "VERIFY",
                    request.getDeviceKey().getDevicePublicKey()
            );

            return NonceResponse.builder()
                    .encryptedNonce(encryptedNonce)
                    .encryptedDecryptionKey(encryptedDecryptionKey)
                    .encryptedVerificationKey(encryptedVerificationKey)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: Invalid public key");
        }
    }

    @Transactional
    public NonceEntity validateAndConsumeNonce(String deviceId, String nonceB64) {

        String rawNonce  = cryptoUtil.decodeBase64(nonceB64);
        String nonceHash = cryptoUtil.hashNonce(rawNonce);

        NonceEntity entity = nonceRepo
                .findByDeviceIdAndNonceHashAndUsedFalse(deviceId, nonceHash)
                .orElseThrow(() -> new RuntimeException("Invalid or expired nonce"));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Nonce has expired");
        }

        entity.setUsed(true);
        nonceRepo.save(entity);
        return entity;
    }

    @Scheduled(fixedDelay = 300_000)
    @Transactional
    public void cleanup() {
        nonceRepo.deleteExpiredNonces(LocalDateTime.now());
        log.debug("Expired nonces cleaned up");
    }
}