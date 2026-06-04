package com.pot.app.sagaorchestrator.grpc;

import com.pot.app.proto.saga.PaymentGrpcRequest;
import com.pot.app.proto.saga.PaymentGrpcResponse;
import com.pot.app.proto.saga.SagaOrchestratorGrpc.SagaOrchestratorImplBase;
import com.pot.app.sagaorchestrator.dto.PaymentSagaResponse;
import com.pot.app.sagaorchestrator.mapping.SagaGrpcMapper;
import com.pot.app.sagaorchestrator.service.SagaOrchestrator;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class SagaGrpcService extends SagaOrchestratorImplBase {

    private final SagaGrpcMapper mapper;
    private final SagaOrchestrator sagaOrchestrator;

    @Override
    public void sagaPayment(PaymentGrpcRequest request, StreamObserver<PaymentGrpcResponse> responseObserver) {
        PaymentSagaResponse response = sagaOrchestrator.startPaymentSaga(mapper.toDto(request));

        responseObserver.onNext(mapper.toGrpc(response));
        responseObserver.onCompleted();
    }
}
