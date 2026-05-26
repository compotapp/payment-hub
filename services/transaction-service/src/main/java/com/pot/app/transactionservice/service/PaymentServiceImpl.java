package com.pot.app.transactionservice.service;

import com.pot.app.shared.dto.event.PaymentEvent;
import com.pot.app.transactionservice.client.grpc.AccountGrpcClient;
import com.pot.app.transactionservice.client.rest.NotificationRestClient;
import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.dto.PaymentResponse;
import com.pot.app.transactionservice.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pot.app.shared.util.MoneyConverter.toMinorUnit;
import static com.pot.app.transactionservice.constans.TransactionStatus.FAILED_STATUS;
import static com.pot.app.transactionservice.constans.TransactionStatus.SUCCESS_STATUS;
import static com.pot.app.transactionservice.constans.TransactionType.PAYMENT_TYPE;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TransactionService transactionService;
    private final AccountGrpcClient accountGrpcClient;
    private final NotificationRestClient notificationRestClient;
    private final OutboxService outboxService;

    @Override
    @Transactional
    public PaymentResponse payment(PaymentRequest request) {
        Transaction transaction = transactionService.create(request);
        outboxService.create(transaction);

        String reservationId = accountGrpcClient.reserveFunds(
                transaction.getTransactionId(),
                transaction.getUserId(),
                toMinorUnit(transaction.getAmount())
        );

        if (reservationId != null) {
            transaction.setStatus(SUCCESS_STATUS);
            notificationRestClient.sendEmail(
                    transaction.getUserId(),
                    PAYMENT_TYPE,
                    "amount success: " + transaction.getAmount(),
                    transaction.getTransactionId());
            transaction = transactionService.update(transaction);
        } else {
            transaction.setStatus(FAILED_STATUS);
            notificationRestClient.sendEmail(
                    transaction.getUserId(),
                    PAYMENT_TYPE,
                    "amount failed: " + transaction.getAmount(),
                    transaction.getTransactionId());
            transaction = transactionService.update(transaction);
        }
        return new PaymentResponse(transaction.getTransactionId(), transaction.getStatus(), "asd");
    }
}
