package com.pot.app.transactionservice.integration.grpc;

import com.pot.app.proto.saga.PaymentGrpcRequest;
import com.pot.app.proto.saga.PaymentGrpcResponse;
import com.pot.app.proto.saga.SagaOrchestratorGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static com.pot.app.shared.constans.SagaConstants.SagaStatus.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaOrchestratorGrpcClientTest {

    @Mock
    private SagaOrchestratorGrpc.SagaOrchestratorBlockingStub sagaStub;

    private SagaOrchestratorGrpcClient client;

    @BeforeEach
    void setUp() {
        client = new SagaOrchestratorGrpcClient();
        ReflectionTestUtils.setField(client, "sagaStub", sagaStub);
    }

    @Test
    @DisplayName("Should return success response when gRPC call succeeds")
    void sagaPayment_WhenSuccess_ReturnsResponse() {
        //given
        String sagaId = "saga1";
        String transactionId = "tx123";
        String userId = "user1";
        BigDecimal amount = new BigDecimal("150.50");

        PaymentGrpcResponse expectedResponse = PaymentGrpcResponse.newBuilder()
                .setSagaId(sagaId)
                .setStatus(STARTED)
                .setMessage("Saga created, processing started")
                .setSuccess(true)
                .build();

        when(sagaStub.sagaPayment(any(PaymentGrpcRequest.class))).thenReturn(expectedResponse);

        // when
        PaymentGrpcResponse actualResponse = client.sagaPayment(transactionId, userId, amount);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getSagaId()).isEqualTo(sagaId);
        assertThat(actualResponse.getStatus()).isEqualTo(STARTED);
        assertThat(actualResponse.getMessage()).isEqualTo("Saga created, processing started");
        assertThat(actualResponse.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should throw RuntimeException when gRPC returns success=false")
    void sagaPayment_WhenResponseSuccessFalse_ThrowsException() {
        // given
        PaymentGrpcResponse failedResponse = PaymentGrpcResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Insufficient funds")
                .build();

        when(sagaStub.sagaPayment(any(PaymentGrpcRequest.class)))
                .thenReturn(failedResponse);

        // when & then
        assertThatThrownBy(() -> client.sagaPayment("txn1", "user1", BigDecimal.TEN))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Saga payment failed")
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    @DisplayName("Should throw RuntimeException when gRPC call throws StatusRuntimeException")
    void sagaPayment_WhenGrpcThrowsException_ThrowsRuntimeException() {
        // given
        when(sagaStub.sagaPayment(any(PaymentGrpcRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE.withDescription("Service down")));

        // when & then
        assertThatThrownBy(() -> client.sagaPayment("txn1", "user1", BigDecimal.TEN))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("UNAVAILABLE: Service down");
    }
}