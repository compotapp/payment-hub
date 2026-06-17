package com.pot.app.accountservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static com.pot.app.shared.constans.KafkaTopics.SAGA_COMMANDS;
import static com.pot.app.shared.constans.KafkaTopics.SAGA_EVENTS;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class ProducerKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //config.put(INTERCEPTOR_CLASSES_CONFIG, "io.opentelemetry.instrumentation.kafka.v2_6.TracingProducerInterceptor");
        config.put(ACKS_CONFIG, "all"); // Гарантия, что сообщение не потеряется (ждём всех реплик)
        config.put(ENABLE_IDEMPOTENCE_CONFIG, true); // Защита от дубликатов при ретраях
        config.put(RETRIES_CONFIG, 10); // Максимальное количество ретраев (можно оставить бесконечным Integer.MAX_VALUE)
        config.put(RETRY_BACKOFF_MS_CONFIG, 1000); // 1 секунда. Пауза между ретраями (100ms default, можно увеличить)
        config.put(DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 2 минуты. Тайм-аут на доставку (чтобы не висеть вечно)
        config.put(REQUEST_TIMEOUT_MS_CONFIG, 30000); // 30 секунд. Тайм-аут ожидания ответа от брокера
        return config;
    }

    @Bean
    ProducerFactory<String, Object> stringProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    KafkaTemplate<String, Object> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }
}

//RETRIES_CONFIG Повторная отправка сообщения при таких ошибках:
//
//Ситуация	Будет retry?
//Лидер партиции временно недоступен	✅ Да
//NotEnoughReplicasException (не хватает реплик)	✅ Да
//TimeoutException (запрос не успел)	✅ Да
//NetworkException (обрыв сети)	✅ Да
//LeaderNotAvailableException (выборы лидера)	✅ Да
//Сериализация не удалась	❌ Нет (ошибка данных)
//Топик не существует	❌ Нет
//Сообщение слишком большое (RecordTooLargeException)	❌ Нет
//Авторизация не пройдена	❌ Нет
