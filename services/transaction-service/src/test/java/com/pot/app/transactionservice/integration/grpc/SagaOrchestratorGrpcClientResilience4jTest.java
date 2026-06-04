//package com.pot.app.transactionservice.integration.grpc;
//
//import com.pot.app.proto.saga.PaymentGrpcResponse;
//import com.pot.app.proto.saga.SagaOrchestratorGrpc;
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.math.BigDecimal;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SagaOrchestratorGrpcClientResilience4jTest {
//
//    @Mock
//    private SagaOrchestratorGrpc.SagaOrchestratorBlockingStub sagaStub;
//
//    @Mock
//    private CircuitBreakerRegistry circuitBreakerRegistry;
//
//    @Mock
//    private CircuitBreaker circuitBreaker;
//
//    private SagaOrchestratorGrpcClient client;
//
//    @BeforeEach
//    void setUp() {
//        client = new SagaOrchestratorGrpcClient();
//        ReflectionTestUtils.setField(client, "sagaStub", sagaStub);
//    }
//
//    @Test
//    @DisplayName("Circuit breaker should open after multiple failures")
//    void circuitBreaker_ShouldOpenAfterFailures() throws Exception {
//        // given - мокаем постоянные ошибки
//        when(sagaStub.sagaPayment(any()))
//                .thenThrow(new RuntimeException("gRPC service unavailable"));
//
//        // when - вызываем метод много раз
//        for (int i = 0; i < 10; i++) {
//            try {
//                client.sagaPayment("txn_" + i, "user1", BigDecimal.TEN);
//            } catch (RuntimeException e) {
//                // ожидаем исключения
//            }
//        }
//
//        System.out.println();
//        // then - проверяем что circuit breaker перешел в OPEN состояние
//        // (требуется доступ к CircuitBreakerRegistry)
//         CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
//         assertThat(metrics.getFailureRate()).isGreaterThan(50);
//    }
//
//    @Test
//    @DisplayName("Fallback should be called when circuit breaker is open")
//    void circuitBreaker_WhenOpen_CallsFallback() {
//        // given
//        when(sagaStub.sagaPayment(any()))
//                .thenThrow(new RuntimeException("Service down"));
//
//        // when - вызываем пока circuit breaker не откроется
//        // then - fallback должен вернуть дефолтный ответ
//        PaymentGrpcResponse response = null;
//        for (int i = 0; i < 15; i++) {
//            try {
//                response = client.sagaPayment("txn1", "user1", BigDecimal.TEN);
//            } catch (Exception e) {
//                // продолжаем
//            }
//        }
//
//        // После открытия circuit breaker, fallback должен вернуть response
//        if (response != null) {
//            assertThat(response.getSuccess()).isFalse();
//            assertThat(response.getMessage()).contains("failed after retries");
//        }
//    }
//}