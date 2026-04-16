package com.pot.app.accountservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Включает аудит постоянных(persistent) сущностей.
 * Включает @CreatedDate, @LastModifiedDate
 */
//не используется
@Configuration
@EnableJpaAuditing
public class DataConfig {
}