package com.pot.app.shared.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Утилита для работы с JSON.
 * Все сервисы используют один ObjectMapper с одинаковыми настройками.
 */
@Slf4j
public final class JsonUtils {

    // Единый ObjectMapper для всего проекта
    @Getter
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // Регистрируем модуль для работы с Java 8+ временем (Instant, LocalDateTime)
        objectMapper.registerModule(new JavaTimeModule());
        // Отключаем запись дат как timestamp (используем ISO-формат)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtils() {
        // Запрещаем создание экземпляров
    }

    /**
     * Преобразует объект в JSON строку.
     *
     * @param object объект для сериализации
     * @return JSON строка или null в случае ошибки
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Преобразует JSON строку в объект указанного класса.
     *
     * @param json JSON строка
     * @param clazz целевой класс
     * @param <T> тип объекта
     * @return десериализованный объект или null в случае ошибки
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Преобразует JSON строку в объект с безопасной обработкой ошибок.
     * Вместо null выбрасывает RuntimeException.
     */
    public static <T> T fromJsonOrThrow(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> toMap(Object object) {
        try {
            // Конвертируем объект напрямую в Map через TypeReference
            return objectMapper.convertValue(object, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Ошибка конвертации объекта в Map", e);
        }
    }
}
