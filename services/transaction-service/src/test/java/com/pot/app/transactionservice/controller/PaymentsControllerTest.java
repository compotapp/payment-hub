package com.pot.app.transactionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.dto.PaymentResponse;
import com.pot.app.transactionservice.exception.GlobalExceptionHandler;
import com.pot.app.transactionservice.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentsController.class)
@Import(GlobalExceptionHandler.class) // Импортируем наш хендлер
class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("Should return 200 OK when payment request is valid")
    void create_ValidRequest_ReturnsOk() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", new BigDecimal("100.50"));
        PaymentResponse response = new PaymentResponse("pay_001", "saga_1", "SUCCESS", "Payment processed");

        when(paymentService.payment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("pay_001"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Payment processed"));
    }

    @Test
    @DisplayName("Should return 400 with validation errors when userId is null")
    void create_UserIdIsNull_ReturnsValidationError() throws Exception {
        PaymentRequest request = new PaymentRequest(null, new BigDecimal("100.50"));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.userId").value("UserId is required"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments"))
                .andExpect(jsonPath("$.correlationId").exists());
    }

    @Test
    @DisplayName("Should return 400 with validation errors when userId is blank")
    void create_UserIdIsBlank_ReturnsValidationError() throws Exception {
        PaymentRequest request = new PaymentRequest("", new BigDecimal("100.50"));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.userId").value("UserId is required"));
    }

    @Test
    @DisplayName("Should return 400 with validation errors when amount is null")
    void create_AmountIsNull_ReturnsValidationError() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", null);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.amount").value("Amount is required"));
    }

    @Test
    @DisplayName("Should return 400 when amount is zero")
    void create_AmountIsZero_ReturnsValidationError() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.amount").value("Amount must be positive"));
    }

    @Test
    @DisplayName("Should return 400 when amount is negative")
    void create_AmountIsNegative_ReturnsValidationError() throws Exception {
        PaymentRequest request = new PaymentRequest("user123", new BigDecimal("-50.00"));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.amount").value("Amount must be positive"));
    }

    @Test
    @DisplayName("Should return 400 with multiple validation errors")
    void create_MultipleValidationErrors_ReturnsBothErrors() throws Exception {
        PaymentRequest request = new PaymentRequest("", null);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.userId").value("UserId is required"))
                .andExpect(jsonPath("$.validationErrors.amount").value("Amount is required"));
    }
}