package com.example.nonceapi.controller;

import com.example.nonceapi.model.NonceRequest;
import com.example.nonceapi.model.NonceResponse;
import com.example.nonceapi.service.NonceService;
import com.example.nonceapi.util.DateHeaderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2")
public class NonceController {

    private final NonceService nonceService;
    private final DateHeaderUtil dateHeaderUtil;

    public NonceController(NonceService nonceService, DateHeaderUtil dateHeaderUtil) {
        this.nonceService   = nonceService;
        this.dateHeaderUtil = dateHeaderUtil;
    }

    /**
     * POST /api/v2/generateNonce
     *
     * Headers:
     *   Content-Type  : application/json
     *   Accept        : */*
     *   Date          : <hex encoded millis>  e.g. "0000019d47ffe6f2"  ← REQUIRED
     *   x-request-id  : any-unique-id  (optional)
     *
     * Date header rules:
     *   - Current time millis -> hex string
     *   - Server validates: diff must be within ±30 seconds
     *   - If tampered / too old / future -> 400 rejected
     *
     * Request Body:
     * {
     *   "deviceId"    : "device-abc-123",   (required)
     *   "appId"       : "com.example.app",  (required)
     *   "deviceMake"  : "Samsung",          (optional)
     *   "deviceModel" : "Galaxy S21",       (optional)
     *   "osVersion"   : "13",               (optional)
     *   "appVersion"  : "1.0.0"            (optional)
     * }
     */
    @PostMapping("/generateNonce")
    public ResponseEntity<NonceResponse> generateNonce(
            @Valid @RequestBody NonceRequest request,
            @RequestHeader(value = "Date", required = false) String dateHeader,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId) {

        // 1. Date header required check
        if (dateHeader == null || dateHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    NonceResponse.builder()
                            .status("FAILURE")
                            .errorCode("400")
                            .message("Date header is required. Send current time as hex millis. Example: "
                                    + dateHeaderUtil.generateHexDate())
                            .build());
        }

        // 2. ±30 seconds validation
        if (!dateHeaderUtil.isDateValid(dateHeader)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    NonceResponse.builder()
                            .status("FAILURE")
                            .errorCode("400")
                            .message("Date header is invalid or outside ±30 seconds window. Current hex: "
                                    + dateHeaderUtil.generateHexDate())
                            .build());
        }

        // 3. Auto x-request-id
        if (xRequestId == null || xRequestId.isEmpty())
            xRequestId = UUID.randomUUID().toString();

        // 4. Generate nonce
        NonceResponse response = nonceService.generateNonce(request, xRequestId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v2/health
     * Returns current hex Date value — copy this for Postman testing
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Nonce API is running | Use this Date header: "
                + dateHeaderUtil.generateHexDate());
    }
}
