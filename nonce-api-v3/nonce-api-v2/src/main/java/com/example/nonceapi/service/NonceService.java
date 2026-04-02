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

        NonceEntity entity = NonceEntity.builder()
                .deviceId(request.getDeviceId())
                .appId(request.getAppId())
                .nonceHash(nonceHash)
                .createdAt(now)
                .expiresAt(expiresAt)
                .used(false)
                .xRequestId(xRequestId)
                .build();
        nonceRepo.save(entity);

        log.info("Nonce generated | deviceId={} appId={} xRequestId={}",
                request.getDeviceId(), request.getAppId(), xRequestId);

        return NonceResponse.builder()
                .status("SUCCESS")
                .nonce(cryptoUtil.encodeBase64(rawNonce))
                .deviceId(request.getDeviceId())
                .appId(request.getAppId())
                .expiresAt(expiresAt.format(FMT))
                .xRequestId(xRequestId)
                .build();
    }

    /** Validate nonce — used by token API later */
    @Transactional
    public NonceEntity validateAndConsumeNonce(String deviceId, String appId, String nonceB64) {
        String rawNonce  = cryptoUtil.decodeBase64(nonceB64);
        String nonceHash = cryptoUtil.hashNonce(rawNonce);

        NonceEntity entity = nonceRepo
                .findByDeviceIdAndAppIdAndNonceHashAndUsedFalse(deviceId, appId, nonceHash)
                .orElseThrow(() -> new RuntimeException("Invalid or expired nonce"));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Nonce has expired");

        entity.setUsed(true);
        nonceRepo.save(entity);
        return entity;
    }

    /** Cleanup expired nonces every 5 minutes */
    @Scheduled(fixedDelay = 300_000)
    @Transactional
    public void cleanup() {
        nonceRepo.deleteExpiredNonces(LocalDateTime.now());
        log.debug("Expired nonces cleaned up");
    }
}
