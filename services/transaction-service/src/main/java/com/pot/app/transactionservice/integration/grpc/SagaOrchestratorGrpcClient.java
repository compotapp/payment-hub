package com.pot.app.transactionservice.integration.grpc;

import com.pot.app.proto.saga.PaymentGrpcRequest;
import com.pot.app.proto.saga.PaymentGrpcResponse;
import com.pot.app.proto.saga.SagaOrchestratorGrpc.SagaOrchestratorBlockingStub;
import com.pot.app.shared.util.MoneyConverter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


@Slf4j
@Component
@RequiredArgsConstructor
public class SagaOrchestratorGrpcClient {

    @GrpcClient("saga-orchestrator")
    private SagaOrchestratorBlockingStub sagaStub;

    private static final String GRPC_SERVICE = "sagaGrpcService";

    @CircuitBreaker(name = GRPC_SERVICE, fallbackMethod = "fallbackSagaPayment")
    @Retry(name = GRPC_SERVICE, fallbackMethod = "fallbackSagaPayment")
    public PaymentGrpcResponse sagaPayment(String transactionId, String userId, BigDecimal amount) {
        log.info("Calling saga payment via gRPC: transactionId={}, userId={}, amount={}", transactionId, userId, amount);

        PaymentGrpcRequest request = PaymentGrpcRequest.newBuilder()
                .setTransactionId(transactionId)
                .setUserId(userId)
                .setAmount(MoneyConverter.toMinorUnit(amount))
                .build();

        PaymentGrpcResponse response = sagaStub
                .withDeadlineAfter(5, SECONDS)
                .sagaPayment(request);

        if (response.getSuccess()) {
            log.info("Saga payment successful: message={}", response.getMessage());
            return response;
        } else {
            log.warn("Saga payment failed: {}", response.getMessage());
            throw new RuntimeException("Saga payment failed: " + response.getMessage());
        }
    }

    // Fallback метод для синхронных вызовов
    private PaymentGrpcResponse fallbackSagaPayment(
            String transactionId,
            String userId,
            BigDecimal amount,
            Throwable ex
    ) {

        log.error("Fallback executed for sagaPayment: transactionId={}, error={}",
                transactionId, ex.getMessage());

        return PaymentGrpcResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Payment failed after retries: " + ex.getMessage())
                .build();
    }

    // Асинхронная версия с TimeLimiter
    @TimeLimiter(name = GRPC_SERVICE, fallbackMethod = "fallbackAsyncPayment")
    public CompletableFuture<PaymentGrpcResponse> sagaPaymentAsync(
            String transactionId,
            String userId,
            BigDecimal amount) {

        return CompletableFuture.supplyAsync(() ->
                sagaPayment(transactionId, userId, amount)
        );
    }

    // Fallback для асинхронного вызова
    private CompletableFuture<PaymentGrpcResponse> fallbackAsyncPayment(
            String transactionId,
            String userId,
            BigDecimal amount,
            Throwable ex) {

        log.error("Async fallback: {}", ex.getMessage());
        return CompletableFuture.completedFuture(
                PaymentGrpcResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Async payment failed: " + ex.getMessage())
                        .build()
        );
    }
}
