package com.pot.app.sagaorchestrator.mapping;

import com.pot.app.sagaorchestrator.dto.PaymentSagaRequest;
import com.pot.app.sagaorchestrator.dto.PaymentSagaResponse;
import com.pot.app.sagaorchestrator.entity.SagaInstance;
import com.pot.app.shared.util.JsonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;
import java.util.UUID;

import static com.pot.app.shared.constans.SagaConstants.SagaType.PAYMENT_SAGA;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = WARN,
        imports = {UUID.class}

)
public interface SagaInstanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sagaType", constant = PAYMENT_SAGA)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentStep", ignore = true)
    @Mapping(target = "payload", expression = "java(toMap(request))")
    @Mapping(target = "context", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SagaInstance toEntity(PaymentSagaRequest request);

    default PaymentSagaResponse toDto(SagaInstance sagaInstance) {
        return new PaymentSagaResponse(
                sagaInstance.getId().toString(),
                sagaInstance.getStatus(),
                "Create saga",
                true
        );
    }

    default Map<String, Object> toMap(PaymentSagaRequest request) {
        return JsonUtils.toMap(request);
    }
}
