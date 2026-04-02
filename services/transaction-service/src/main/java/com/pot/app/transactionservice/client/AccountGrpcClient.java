package com.pot.app.transactionservice.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * gRPC клиент для вызова Account Service.
 * Библиотека grpc-spring-boot-starter автоматически создаст бин
 * для AccountServiceGrpc.AccountServiceBlockingStub.
 */
@Component
public class AccountGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(AccountGrpcClient.class);

    /**
     * @GrpcClient("account-service") — название сервиса из конфигурации.
     * Будет создан автоматически с настройками из application.yml.
     */
    @GrpcClient("account-service")
    private com.pot.app.proto.account.AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    /**
     * Резервирование средств.
     * Возвращает reservationId или null в случае ошибки.
     */
    public String reserveFunds(String transactionId, String userId, long amount) {
        log.info("Calling gRPC AccountService.reserveFunds: tx={}, userId={}, amount={}",
                transactionId, userId, amount);

        try {
            com.pot.app.proto.account.ReserveRequest request = com.pot.app.proto.account.ReserveRequest.newBuilder()
                    .setTransactionId(transactionId)
                    .setUserId(userId)
                    .setAmount(amount)
                    .build();

            com.pot.app.proto.account.ReserveResponse response = accountStub.reserveFunds(request);

            if (response.getSuccess()) {
                log.info("Reservation successful: reservationId={}", response.getReservationId());
                return response.getReservationId();
            } else {
                log.warn("Reservation failed: {}", response.getMessage());
                return null;
            }

        } catch (Exception e) {
            log.error("gRPC call failed", e);
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