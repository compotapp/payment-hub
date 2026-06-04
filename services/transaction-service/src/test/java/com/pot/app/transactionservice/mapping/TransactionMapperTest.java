package com.pot.app.transactionservice.mapping;

import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.entity.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.pot.app.transactionservice.constans.TransactionStatus.PENDING;
import static com.pot.app.transactionservice.constans.TransactionType.PAYMENT;
import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapperImpl();

    @Test
    @DisplayName("Should map PaymentRequest to Transaction correctly")
    void toEntity_ShouldMapAllFields() {
        PaymentRequest request = new PaymentRequest("user123", new BigDecimal("99.99"));

        Transaction transaction = mapper.toEntity(request);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isNull();
        assertThat(transaction.getTransactionId()).isNotNull();
        assertThat(transaction.getUserId()).isEqualTo("user123");
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(transaction.getStatus()).isEqualTo(PENDING);
        assertThat(transaction.getType()).isEqualTo(PAYMENT);
        assertThat(transaction.getMetadata().isEmpty()).isTrue();
        assertThat(transaction.getCreatedAt()).isNull();
        assertThat(transaction.getUpdatedAt()).isNull();
    }
}