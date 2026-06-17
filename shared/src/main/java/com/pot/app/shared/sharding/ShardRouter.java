package com.pot.app.shared.sharding;

public class ShardRouter {
    
    private static final int NUMBER_OF_SHARDS = 2;
    
    /**
     * Определяет номер шарда по user_id.
     * Один и тот же user_id всегда попадает в один шард.
     */
    public static int getShardIndex(String userId) {
        // Используем hashCode и модуль
        return Math.abs(userId.hashCode()) % NUMBER_OF_SHARDS;
    }
    
    /**
     * Возвращает номер шарда для конкретного сервиса.
     * В реальном проекте можно использовать разные конфигурации для разных сервисов.
     */
    public static int getTransactionShardIndex(String userId) {
        return getShardIndex(userId);
    }
    
    public static int getAccountShardIndex(String userId) {
        return getShardIndex(userId);
    }
}