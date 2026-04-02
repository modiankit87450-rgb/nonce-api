package com.example.nonceapi.repository;

import com.example.nonceapi.entity.NonceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NonceRepository extends JpaRepository<NonceEntity, Long> {

    Optional<NonceEntity> findByDeviceIdAndAppIdAndNonceHashAndUsedFalse(
            String deviceId, String appId, String nonceHash);

    @Modifying
    @Transactional
    @Query("DELETE FROM NonceEntity n WHERE n.expiresAt < :now")
    void deleteExpiredNonces(LocalDateTime now);
}
