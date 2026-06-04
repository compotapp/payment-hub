package com.pot.app.transactionservice.service;

import com.pot.app.proto.saga.PaymentGrpcResponse;
import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.dto.PaymentResponse;
import com.pot.app.transactionservice.entity.Transaction;
import com.pot.app.transactionservice.integration.grpc.AccountGrpcClient;
import com.pot.app.transactionservice.integration.grpc.SagaOrchestratorGrpcClient;
import com.pot.app.transactionservice.integration.rest.NotificationRestClient;
import com.pot.app.transactionservice.metrics.PaymentMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pot.app.transactionservice.constans.TransactionStatus.PROCESSING;
import static com.pot.app.transactionservice.constans.TransactionStatus.SUCCESS;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMetrics paymentMetrics;
    private final TransactionService transactionService;
    private final SagaOrchestratorGrpcClient sagaGrpcClient;
    private final AccountGrpcClient accountGrpcClient;
    private final NotificationRestClient notificationRestClient;
    private final OutboxService outboxService;

    @Override
    @Transactional
    public PaymentResponse payment(PaymentRequest request) {
        Transaction transaction = transactionService.create(request);
        PaymentGrpcResponse response = sagaGrpcClient.sagaPayment(
                transaction.getTransactionId(),
                transaction.getUserId(),
                transaction.getAmount()
        );

        if (response.getSuccess()) {
            transaction.setStatus(SUCCESS);
            transaction = transactionService.update(transaction);
            paymentMetrics.recordSuccess();
            return new PaymentResponse(
                    transaction.getTransactionId(),
                    response.getSagaId(),
                    transaction.getStatus(),
                    PROCESSING
            );
        } else {
            paymentMetrics.recordFailure();
            throw new RuntimeException(response.getMessage());
        }

//        return paymentMetrics.recordProcessingTime(() -> {
//                    if (response.getSuccess()) {
//                        paymentMetrics.recordSuccess();
//                    } else {
//                        paymentMetrics.recordFailure();
//                    }
//                    return new PaymentResponse(transaction.getTransactionId(), transaction.getStatus(), "asd");
//                }
//        );
    }

//    @Override
//    @Transactional
//    public PaymentResponse payment(PaymentRequest request) {
//        Transaction transaction = transactionService.create(request);
//        outboxService.create(transaction);
//
//        String reservationId = accountGrpcClient.reserveFunds(
//                transaction.getTransactionId(),
//                transaction.getUserId(),
//                toMinorUnit(transaction.getAmount())
//        );
//
//        if (reservationId != null) {
//            transaction.setStatus(SUCCESS_STATUS);
//            notificationRestClient.sendEmail(
//                    transaction.getUserId(),
//                    PAYMENT_TYPE,
//                    "amount success: " + transaction.getAmount(),
//                    transaction.getTransactionId());
//            transaction = transactionService.update(transaction);
//        } else {
//            transaction.setStatus(FAILED_STATUS);
//            notificationRestClient.sendEmail(
//                    transaction.getUserId(),
//                    PAYMENT_TYPE,
//                    "amount failed: " + transaction.getAmount(),
//                    transaction.getTransactionId());
//            transaction = transactionService.update(transaction);
//        }
//        return new PaymentResponse(transaction.getTransactionId(), transaction.getStatus(), "asd");
//    }
}
