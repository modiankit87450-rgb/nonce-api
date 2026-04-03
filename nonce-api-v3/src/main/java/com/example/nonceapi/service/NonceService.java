package com.example.nonceapi.service;

import com.example.nonceapi.entity.NonceEntity;
import com.example.nonceapi.model.NonceRequest;
import com.example.nonceapi.model.NonceResponse;
import com.example.nonceapi.repository.NonceRepository;
import com.example.nonceapi.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class NonceService {

    private static final Logger log = LoggerFactory.getLogger(NonceService.class);

    private final NonceRepository nonceRepo;
    private final CryptoUtil cryptoUtil;

    public NonceService(NonceRepository nonceRepo, CryptoUtil cryptoUtil) {
        this.nonceRepo = nonceRepo;
        this.cryptoUtil = cryptoUtil;
    }

    @Transactional
    public NonceResponse generateNonce(NonceRequest request, String xRequestId) {

        String rawNonce  = cryptoUtil.generateRawNonce();
        String nonceHash = cryptoUtil.hashNonce(rawNonce);

        // Find existing device row or create new one
        NonceEntity entity = nonceRepo.findByDeviceId(request.getId())
                .orElse(new NonceEntity());

        // Store / update device data
        entity.setDeviceId(request.getId());
        entity.setAppId(request.getAppId());
        entity.setNonceHash(nonceHash);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setXRequestId(xRequestId);
        entity.setCameraResolutionFront(request.getCameraResolutionFront());
        entity.setCameraResolutionBack(request.getCameraResolutionBack());
        entity.setCpuAbi(request.getCpuAbi());
        entity.setDeviceModel(request.getDeviceModel());
        entity.setDeviceSDKVer(request.getDeviceSDKVer());
        entity.setDeviceScreenSize(request.getDeviceScreenSize());
        entity.setManufacturer(request.getManufacturer());
        entity.setProcessor(request.getProcessor());
        entity.setAvailableRam(request.getAvailableRam());
        entity.setDeviceOs(request.getDeviceOs());
        entity.setDeviceOsVer(request.getDeviceOsVer());

        if (request.getDeviceKey() != null) {
            entity.setDevicePublicKey(request.getDeviceKey().getDevicePublicKey());
            entity.setDeviceUserName(request.getDeviceKey().getUserName());
            entity.setDevicePassword(request.getDeviceKey().getPassword());
        }

        nonceRepo.save(entity);

        log.info("Device data saved | deviceId={} xRequestId={}", request.getId(), xRequestId);

        try {
            // AES key
            var aesKey = cryptoUtil.generateAESKey();

            // Encrypt nonce with AES
            String encryptedNonce = cryptoUtil.encryptWithAES(rawNonce, aesKey);

            // Encrypt AES key with device public key
            String encryptedDecryptionKey = cryptoUtil.encryptWithPublicKey(
                    Base64.getEncoder().encodeToString(aesKey.getEncoded()),
                    request.getDeviceKey().getDevicePublicKey()
            );

            // Encrypt verification key with device public key
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
}