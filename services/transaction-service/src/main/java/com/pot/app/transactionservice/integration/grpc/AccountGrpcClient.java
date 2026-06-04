package com.pot.app.transactionservice.integration.grpc;

import com.pot.app.proto.account.AccountServiceGrpc.AccountServiceBlockingStub;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * gRPC клиент для вызова Account Service.
 * Библиотека grpc-spring-boot-starter автоматически создаст бин
 * для AccountServiceGrpc.AccountServiceBlockingStub.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AccountGrpcClient {

    private final CircuitBreaker circuitBreaker;
    private final TimeLimiter timeLimiter;

    /**
     * @GrpcClient("account-service") — название сервиса из конфигурации.
     * Будет создан автоматически с настройками из application.yml.
     */
    @GrpcClient("account-service")
    private AccountServiceBlockingStub accountStub;

    /**
     * Резервирование средств.
     * Возвращает reservationId или null в случае ошибки.
     */
    public String reserveFunds(String transactionId, String userId, long amount) {
        log.info("Calling gRPC AccountService.reserveFunds: tx={}, userId={}, amount={}",
                transactionId, userId, amount);

        try {
            Supplier<String> grpcCall = () -> {
                com.pot.app.proto.account.ReserveRequest request = com.pot.app.proto.account.ReserveRequest.newBuilder()
                        .setTransactionId(transactionId)
                        .setUserId(userId)
                        .setAmount(amount)
                        .build();

                com.pot.app.proto.account.ReserveResponse response = accountStub
                        .withDeadlineAfter(5, SECONDS)
                        .reserveFunds(request);

                if (response.getSuccess()) {
                    log.info("Reservation successful: reservationId={}", response.getReservationId());
                    return response.getReservationId();
                } else {
                    log.warn("Reservation failed: {}", response.getMessage());
                    throw new RuntimeException("Reservation failed: " + response.getMessage());
                }
            };

            Supplier<String> withCircuitBreaker = CircuitBreaker.decorateSupplier(
                    circuitBreaker,
                    grpcCall
            );

            return withCircuitBreaker.get();

        } catch (Exception e) {
            log.error("gRPC call failed", e);
            if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
                log.warn("Circuit Breaker is OPEN, failing fast");
                throw new RuntimeException("Account Service is temporarily unavailable (Circuit Breaker OPEN)", e);
            }
            throw new RuntimeException("Failed to call AccountService", e);
        }
    }

    /**
     * Подтверждение списания.
     */
    public boolean commitFunds(String transactionId, String reservationId) {
        log.info("Calling gRPC AccountService.commitFunds: tx={}, reservationId={}",
                transactionId, reservationId);

        try {
            com.pot.app.proto.account.CommitRequest request = com.pot.app.proto.account.CommitRequest.newBuilder()
                    .setTransactionId(transactionId)
                    .setReservationId(reservationId)
                    .build();

            com.pot.app.proto.account.CommitResponse response = accountStub.commitFunds(request);
            return response.getSuccess();

        } catch (Exception e) {
            log.error("gRPC commit failed", e);
            return false;
        }
    }

    /**
     * Отмена резервирования.
     */
    public boolean cancelReservation(String transactionId, String reservationId) {
        log.info("Calling gRPC AccountService.cancelReservation: tx={}, reservationId={}",
                transactionId, reservationId);

        try {
            com.pot.app.proto.account.CancelRequest request = com.pot.app.proto.account.CancelRequest.newBuilder()
                    .setTransactionId(transactionId)
                    .setReservationId(reservationId)
                    .build();

            com.pot.app.proto.account.CancelResponse response = accountStub.cancelReservation(request);
            return response.getSuccess();

        } catch (Exception e) {
            log.error("gRPC cancel failed", e);
            return false;
        }
    }
}