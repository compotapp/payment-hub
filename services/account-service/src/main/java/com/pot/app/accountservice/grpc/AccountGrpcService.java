package com.pot.app.accountservice.grpc;

import com.pot.app.accountservice.dto.BalanceData;
import com.pot.app.accountservice.dto.ReservationResult;
import com.pot.app.accountservice.service.AccountReservationService;
import com.pot.app.proto.account.AccountServiceGrpc;
import com.pot.app.proto.account.BalanceRequest;
import com.pot.app.proto.account.BalanceResponse;
import com.pot.app.proto.account.CancelRequest;
import com.pot.app.proto.account.CancelResponse;
import com.pot.app.proto.account.CommitRequest;
import com.pot.app.proto.account.CommitResponse;
import com.pot.app.proto.account.ReserveRequest;
import com.pot.app.proto.account.ReserveResponse;
import com.pot.app.shared.util.MoneyConverter;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountReservationService service;

    /**
     * Резервирование средств.
     * Важно: метод должен быть идемпотентным!
     * Если резерв с таким transaction_id уже существует — возвращаем существующий.
     */
    @Override
    public void reserveFunds(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        String transactionId = request.getTransactionId();
        String userId = request.getUserId();
        BigDecimal amount = MoneyConverter.toMajorUnit(request.getAmount());

        log.info("gRPC reserveFunds: transactionId={}, userId={}, amount={}", transactionId, userId, amount);

        try {
            ReservationResult result = service.reserve(transactionId, userId, amount);
            ReserveResponse response = ReserveResponse.newBuilder()
                    .setSuccess(result.success())
                    .setReservationId(result.reservationId())
                    .setMessage(result.message())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in reserveFunds", e);
            // Отправляем ошибку клиенту
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Подтверждение списания (коммит).
     */
    @Override
    public void commitFunds(CommitRequest request, StreamObserver<CommitResponse> responseObserver) {
        String transactionId = request.getTransactionId();
        String reservationId = request.getReservationId();

        log.info("gRPC commitFunds: transactionId={}, reservationId={}", transactionId, reservationId);

        try {
            boolean success = service.commit(UUID.fromString(transactionId), reservationId);
            CommitResponse response = CommitResponse.newBuilder()
                    .setSuccess(success)
                    .setMessage(success ? "Committed" : "Failed to commit")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in commitFunds", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void cancelReservation(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
        String transactionId = request.getTransactionId();
        String reservationId = request.getReservationId();

        log.info("gRPC cancelReservation: transactionId={}, reservationId={}", transactionId, reservationId);

        try {
            boolean success = service.cancel(UUID.fromString(reservationId), transactionId);
            CancelResponse response = CancelResponse.newBuilder()
                    .setSuccess(success)
                    .setMessage(success ? "Cancelled" : "Failed to cancel")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in cancelReservation", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        String userId = request.getUserId();
        try {
            BalanceData balance = service.getBalance(userId);
            BalanceResponse response = BalanceResponse.newBuilder()
                    .setUserId(userId)
                    .setAvailableBalance(balance.available())
                    .setReservedBalance(balance.reserved())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in getBalance", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}

//@GrpcService
//public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {
//
//    @Override
//    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
//        String userId = request.getUserId();
//
//        try {
//            BalanceData balance = accountService.getBalance(userId);
//
//            BalanceResponse response = BalanceResponse.newBuilder()
//                    .setUserId(userId)
//                    .setAvailableBalance(balance.getAvailable())
//                    .setReservedBalance(balance.getReserved())
//                    .build();
//
//            responseObserver.onNext(response);
//            responseObserver.onCompleted();
//
//        } catch (Exception e) {
//            log.error("Error in getBalance", e);
//            responseObserver.onError(io.grpc.Status.INTERNAL
//                    .withDescription("Internal error: " + e.getMessage())
//                    .asRuntimeException());
//        }
//    }
//}