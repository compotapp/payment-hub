package com.pot.app.transactionservice.client.rest;

import com.pot.app.transactionservice.dto.EmailRequest;
import com.pot.app.transactionservice.dto.NotificationResponse;
import com.pot.app.transactionservice.dto.PushRequest;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.function.Supplier;

/**
 * REST клиент для вызова Notification Service.
 * Используем RestClient (Spring Boot 3.2+).
 */
@Slf4j
@Component
public class NotificationRestClient {

    private final RestClient restClient;
    private final Retry retry;

    public NotificationRestClient(@Value("${notification.service.url}") String notificationServiceUrl,
                                  Retry retry) {
        this.restClient = RestClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
        this.retry = retry;
    }

    /**
     * Отправить email уведомление синхронно.
     * Возвращает true, если запрос принят (202), иначе false.
     */
    public boolean sendEmail(String to, String subject, String body, String transactionId) {
        log.info("Calling REST NotificationService: to={}, subject={}, tx={}", to, subject, transactionId);

        Supplier<Boolean> restCall = () -> {
            try {
                NotificationResponse response = restClient.post()
                        .uri("/api/v1/notifications/email")
                        .body(new EmailRequest(to, subject, body, transactionId))
                        .retrieve()
                        .toEntity(NotificationResponse.class)
                        .getBody();

                log.info("Notification accepted: id={}, status={}",
                        response.notificationId(), response.status());
                return true;
            } catch (Exception e) {
                log.error("REST call to NotificationService failed", e);
                throw new ResourceAccessException("REST call to NotificationService failed");
            }
        };
        Supplier<Boolean> withRetry = Retry.decorateSupplier(retry, restCall);
        return withRetry.get();
    }

    /**
     * Отправить push уведомление.
     */
    public boolean sendPush(String userId, String title, String body, String transactionId) {
        log.info("Calling REST NotificationService: userId={}, title={}, tx={}", userId, title, transactionId);

        try {
            PushRequest request = new PushRequest(userId, title, body, transactionId);

            NotificationResponse response = restClient.post()
                    .uri("/api/v1/notifications/push")
                    .body(request)
                    .retrieve()
                    .toEntity(NotificationResponse.class)
                    .getBody();

            log.info("Push notification accepted: id={}", response.notificationId());
            return true;

        } catch (Exception e) {
            log.error("REST call to NotificationService failed", e);
            return false;
        }
    }
}