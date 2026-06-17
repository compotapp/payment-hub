package com.pot.app.accountservice.consumer;

import com.pot.app.accountservice.dto.ReservationResult;
import com.pot.app.accountservice.service.AccountReservationService;
import com.pot.app.shared.dto.command.SagaCommand;
import com.pot.app.shared.dto.event.SagaEvent;
import com.pot.app.shared.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static com.pot.app.shared.constans.KafkaTopics.SAGA_COMMANDS;
import static com.pot.app.shared.constans.KafkaTopics.SAGA_EVENTS;
import static com.pot.app.shared.constans.SagaConstants.Commands.RESERVE_FUNDS;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountReservationService accountReservationService;

    @KafkaListener(topics = SAGA_COMMANDS)
    public void handleCommand(String message, Acknowledgment ack) {
        SagaCommand command = JsonUtils.fromJson(message, SagaCommand.class);

        SagaEvent event = new SagaEvent();
        event.setSagaId(command.sagaId());
        event.setTransactionId(command.transactionId());
        event.setTimestamp(Instant.now());
        event.setSource("account-service");

        try {
            switch (command.commandType()) {
                case RESERVE_FUNDS -> {
                    ReservationResult reserve = accountReservationService.reserve(
                            command.transactionId(),
                            command.payload().get("userId").toString(),
                            new BigDecimal(command.payload().get("amount").toString())
                    );
                    event.setEventType("FUNDS_RESERVED");
                    event.setSuccess(true);
                    event.setPayload(Map.of("reservationId", reserve.reservationId()));
                }
            }
        } catch (Exception e) {
            event.setSuccess(false);
            event.setEventType("ERROR");
            event.setPayload(Map.of("errorMessage", e.getMessage()));
        }

        // Отправляем событие обратно в оркестратор
        kafkaTemplate.send(SAGA_EVENTS, command.sagaId(), event);
        ack.acknowledge();
    }
}
