package com.pot.app.accountservice.grpc;

import com.pot.app.proto.account.AccountServiceGrpc;
import com.pot.app.proto.account.BalanceRequest;
import com.pot.app.proto.account.BalanceResponse;
import com.pot.app.proto.account.CancelRequest;
import com.pot.app.proto.account.CancelResponse;
import com.pot.app.proto.account.CommitRequest;
import com.pot.app.proto.account.ReserveRequest;
import com.pot.app.proto.account.ReserveResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    @Override
    public void reserveFunds(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        super.reserveFunds(request, responseObserver);
    }

    @Override
    public void commitFunds(CommitRequest request, StreamObserver<com.pot.app.proto.account.CommitResponse> responseObserver) {
        super.commitFunds(request, responseObserver);
    }

    @Override
    public void cancelReservation(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
        super.cancelReservation(request, responseObserver);
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        super.getBalance(request, responseObserver);
    }
}

//@GrpcService
//public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {
//
//    private static final Logger log = LoggerFactory.getLogger(AccountGrpcService.class);
//
//    @Autowired
//    private AccountService accountService; // Ваш сервис с бизнес-логикой (JPA)
//
//    /**
//     * Резервирование средств.
//     * Важно: метод должен быть идемпотентным!
//     * Если резерв с таким transaction_id уже существует — возвращаем существующий.
//     */
//    @Override
//    public void reserveFunds(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
//        String transactionId = request.getTransactionId();
//        String userId = request.getUserId();
//        long amount = request.getAmount();
//
//        log.info("gRPC reserveFunds: transactionId={}, userId={}, amount={}", transactionId, userId, amount);
//
//        try {
//            // Вызываем бизнес-логику (она должна быть идемпотентной)
//            ReservationResult result = accountService.reserve(transactionId, userId, amount);
//
//            ReserveResponse response = ReserveResponse.newBuilder()
//                    .setSuccess(result.isSuccess())
//                    .setReservationId(result.getReservationId())
//                    .setMessage(result.getMessage())
//                    .build();
//
//            responseObserver.onNext(response);
//            responseObserver.onCompleted();
//
//        } catch (Exception e) {
//            log.error("Error in reserveFunds", e);
//            // Отправляем ошибку клиенту
//            responseObserver.onError(io.grpc.Status.INTERNAL
//                    .withDescription("Internal error: " + e.getMessage())
//                    .asRuntimeException());
//        }
//    }
//
//    /**
//     * Подтверждение списания (коммит).
//     */
//    @Override
//    public void commitFunds(CommitRequest request, StreamObserver<CommitResponse> responseObserver) {
//        String transactionId = request.getTransactionId();
//        String reservationId = request.getReservationId();
//
//        log.info("gRPC commitFunds: transactionId={}, reservationId={}", transactionId, reservationId);
//
//        try {
//            boolean success = accountService.commit(transactionId, reservationId);
//
//            CommitResponse response = CommitResponse.newBuilder()
//                    .setSuccess(success)
//                    .setMessage(success ? "Committed" : "Failed to commit")
//                    .build();
//
//            responseObserver.onNext(response);
//            responseObserver.onCompleted();
//
//        } catch (Exception e) {
//            log.error("Error in commitFunds", e);
//            responseObserver.onError(io.grpc.Status.INTERNAL
//                    .withDescription("Internal error: " + e.getMessage())
//                    .asRuntimeException());
//        }
//    }
//
//    /**
//     * Отмена резервирования.
//     */
//    @Override
//    public void cancelReservation(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
//        String transactionId = request.getTransactionId();
//        String reservationId = request.getReservationId();
//
//        log.info("gRPC cancelReservation: transactionId={}, reservationId={}", transactionId, reservationId);
//
//        try {
//            boolean success = accountService.cancel(transactionId, reservationId);
//
//            CancelResponse response = CancelResponse.newBuilder()
//                    .setSuccess(success)
//                    .setMessage(success ? "Cancelled" : "Failed to cancel")
//                    .build();
//
//            responseObserver.onNext(response);
//            responseObserver.onCompleted();
//
//        } catch (Exception e) {
//            log.error("Error in cancelReservation", e);
//            responseObserver.onError(io.grpc.Status.INTERNAL
//                    .withDescription("Internal error: " + e.getMessage())
//                    .asRuntimeException());
//        }
//    }
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