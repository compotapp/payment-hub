package com.pot.app.transactionservice.controller;

import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.dto.PaymentResponse;
import com.pot.app.transactionservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        PaymentResponse response = service.payment(request);
        return ResponseEntity.ok(response);
    }
}
