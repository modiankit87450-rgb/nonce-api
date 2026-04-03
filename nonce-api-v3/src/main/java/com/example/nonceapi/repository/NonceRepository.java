package com.example.nonceapi.repository;

import com.example.nonceapi.entity.NonceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NonceRepository extends JpaRepository<NonceEntity, Long> {

    Optional<NonceEntity> findByDeviceId(String deviceId);
}