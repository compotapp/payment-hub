package com.pot.app.sagaorchestrator.mapping;

import com.pot.app.sagaorchestrator.entity.Outbox;
import com.pot.app.sagaorchestrator.entity.SagaInstance;
import com.pot.app.shared.dto.command.SagaCommand;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.pot.app.shared.constans.SagaConstants.Commands.RESERVE_FUNDS;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = WARN,
        imports = {UUID.class}
)
public interface OutboxMapper {

    default Outbox toEntity(SagaInstance sagaInstance, String transactionId) {
        return Outbox.builder()
                .messageType(RESERVE_FUNDS)
                .sagaId(sagaInstance.getId().toString())
                .transactionId(transactionId)
                .payload(sagaInstance.getPayload())
                .build();
    }

    default SagaCommand toCommand(Outbox outbox) {
        return new SagaCommand(
                outbox.getSagaId(),
                outbox.getMessageType(),
                outbox.getTransactionId(),
                outbox.getPayload(),
                Instant.now(),
                "saga-orchestrator"
        );
    }

    default List<SagaCommand> toCommand(List<Outbox> outboxes) {
        return outboxes.stream()
                .map(this::toCommand)
                .toList();
    }

    default List<UUID> getIds(List<Outbox> outboxes) {
        return outboxes.stream()
                .map(Outbox::getId)
                .toList();
    }
}
