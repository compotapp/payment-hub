package com.pot.app.sagaorchestrator.controller;

import com.pot.app.sagaorchestrator.dto.SagaStatusResponse;
import com.pot.app.sagaorchestrator.service.SagaInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/saga")
@RequiredArgsConstructor
public class SagaController {

    private final SagaInstanceService service;

//    @PostMapping("/payment")
//    public ResponseEntity<SagaResponse> createPaymentSaga(@RequestBody PaymentSagaRequest request) {
//        String sagaId = sagaOrchestrator.startPaymentSaga(request);
//        return ResponseEntity.accepted().body(new SagaResponse(sagaId, "STARTED"));
//    }
    
    @GetMapping("/{sagaId}/status")
    public ResponseEntity<SagaStatusResponse> getStatus(@PathVariable String sagaId) {
        return ResponseEntity.ok(service.findById(sagaId));
    }
}