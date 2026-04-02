package com.pot.app.notificationservice.controller;

import com.pot.app.notificationservice.dto.EmailRequest;
import com.pot.app.notificationservice.dto.NotificationResponse;
import com.pot.app.notificationservice.dto.NotificationStatusResponse;
import com.pot.app.notificationservice.dto.PushRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST контроллер для Notification Service.
 * <p>
 * POST /api/v1/notifications/email   — отправить email
 * POST /api/v1/notifications/push    — отправить push
 * GET /api/v1/notifications/{id}/status — статус уведомления
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification API", description = "Отправка уведомлений")
@RequiredArgsConstructor
public class NotificationController {

    private NotificationService notificationService; // Сервис с логикой

    /**
     * Отправка email уведомления.
     *
     * @param request тело запроса
     * @return 202 Accepted с ID уведомления
     */
    @PostMapping("/email")
    @Operation(summary = "Отправить email уведомление")
    public ResponseEntity<NotificationResponse> sendEmail(@RequestBody EmailRequest request) {
        log.info("REST POST /notifications/email: to={}, subject={}, transactionId={}",
                request.to(), request.subject(), request.transactionId());

        // Генерируем ID для отслеживания
        String notificationId = UUID.randomUUID().toString();

        // Сохраняем в БД статус PENDING и отправляем в Kafka
        // (синхронно возвращаем ID, фактическая отправка асинхронна)
        notificationService.queueEmail(notificationId, request);

        // Возвращаем 202 Accepted (запрос принят в обработку)
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new NotificationResponse(notificationId, "QUEUED"));
    }

    /**
     * Отправка push уведомления.
     */
    @PostMapping("/push")
    @Operation(summary = "Отправить push уведомление")
    public ResponseEntity<NotificationResponse> sendPush(@RequestBody PushRequest request) {
        log.info("REST POST /notifications/push: userId={}, title={}, transactionId={}",
                request.userId(), request.title(), request.transactionId());

        String notificationId = UUID.randomUUID().toString();
        notificationService.queuePush(notificationId, request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new NotificationResponse(notificationId, "QUEUED"));
    }

    /**
     * Проверка статуса уведомления.
     */
    @GetMapping("/{notificationId}/status")
    @Operation(summary = "Получить статус уведомления")
    public ResponseEntity<NotificationStatusResponse> getStatus(@PathVariable String notificationId) {
        log.info("REST GET /notifications/{}/status", notificationId);

        NotificationStatus status = notificationService.getStatus(notificationId);

        return ResponseEntity.ok(new NotificationStatusResponse(
                notificationId,
                status.getState(),      // SENT, FAILED, PENDING
                status.getDetails()
        ));
    }
}
