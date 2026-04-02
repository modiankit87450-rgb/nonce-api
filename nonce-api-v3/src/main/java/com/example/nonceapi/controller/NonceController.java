package com.example.nonceapi.controller;

import com.example.nonceapi.model.NonceRequest;
import com.example.nonceapi.model.NonceResponse;
import com.example.nonceapi.service.NonceService;
import com.example.nonceapi.util.DateHeaderUtil;
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
     */
    @PostMapping("/generateNonce")
    public ResponseEntity<NonceResponse> generateNonce(
            @Valid @RequestBody NonceRequest request,
            @RequestHeader(value = "Date", required = false) String dateHeader,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId) {

        if (dateHeader == null || dateHeader.isEmpty()) {
            throw new RuntimeException("Date header required");
        }


        if (!dateHeaderUtil.isDateValid(dateHeader)) {
            throw new RuntimeException(
                    "Invalid date or outside ±1 minute. Current time: "
                            + dateHeaderUtil.generateCurrentDate()
            );
        }


        if (xRequestId == null || xRequestId.isEmpty()) {
            xRequestId = UUID.randomUUID().toString();
        }

        NonceResponse response = nonceService.generateNonce(request, xRequestId);

        return ResponseEntity.ok(response);
    }
}