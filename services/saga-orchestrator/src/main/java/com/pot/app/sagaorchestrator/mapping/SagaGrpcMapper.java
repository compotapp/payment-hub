package com.pot.app.sagaorchestrator.mapping;

import com.pot.app.proto.saga.PaymentGrpcRequest;
import com.pot.app.proto.saga.PaymentGrpcResponse;
import com.pot.app.sagaorchestrator.dto.PaymentSagaRequest;
import com.pot.app.sagaorchestrator.dto.PaymentSagaResponse;
import org.mapstruct.Mapper;

import java.util.UUID;

import static com.pot.app.shared.util.MoneyConverter.toMajorUnit;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = WARN,
        imports = {UUID.class}
)
public interface SagaGrpcMapper {

    default PaymentSagaRequest toDto(PaymentGrpcRequest request) {
        return new PaymentSagaRequest(
                request.getTransactionId(),
                request.getUserId(),
                toMajorUnit(request.getAmount())
        );
    }

    //    @Mapping(source = "id", target = "sagaId")
    //    @Mapping(target = "message", constant = "Saga create")
    //    @Mapping(target = "success", constant = "true")
    default PaymentGrpcResponse toGrpc(PaymentSagaResponse response) {
        return PaymentGrpcResponse.newBuilder()
                .setSagaId(response.sagaId())
                .setStatus(response.status())
                .setMessage("Saga create")
                .setSuccess(true)
                .build();
    }
}
