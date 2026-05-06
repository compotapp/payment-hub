package com.pot.app.notificationservice.store;

import com.pot.app.notificationservice.store.record.NotificationRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//Аналог бд
@Component
public class NotificationStatusStore {

    private final Map<String, NotificationRecord> store = new ConcurrentHashMap<>();

    public void put(String notificationId, NotificationRecord record) {
        store.put(notificationId, record);
    }

    public Optional<NotificationRecord> get(String notificationId) {
        return Optional.ofNullable(store.get(notificationId));
    }

    public void updateStatus(String notificationId, String status, String details) {
        NotificationRecord record = store.get(notificationId);
        if (record != null) {
            record.setStatus(status);
            record.setDetails(details);
            record.setUpdatedAt(Instant.now());
        }
    }
}
