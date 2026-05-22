package com.pot.app.transactionservice.service;

import com.pot.app.shared.dto.event.PaymentEvent;
import com.pot.app.transactionservice.client.grpc.AccountGrpcClient;
import com.pot.app.transactionservice.client.rest.NotificationRestClient;
import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.dto.PaymentResponse;
import com.pot.app.transactionservice.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pot.app.shared.constans.KafkaTopics.PAYMENT_EVENTS;
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
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Override
    @Transactional
    public PaymentResponse payment(PaymentRequest request) {
        Transaction transaction = transactionService.create(request);

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
            PaymentEvent event = new PaymentEvent(
                    transaction.getTransactionId(),
                    transaction.getUserId(),
                    toMinorUnit(transaction.getAmount()),
                    transaction.getStatus(),
                    "transaction-service"
            );
            kafkaTemplate.send(PAYMENT_EVENTS, event);
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
