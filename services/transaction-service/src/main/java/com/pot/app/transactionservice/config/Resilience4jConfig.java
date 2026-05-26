package com.pot.app.transactionservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreaker accountServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("accountServiceCircuitBreaker");
    }

    @Bean
    public TimeLimiter accountServiceTimeLimiter(TimeLimiterRegistry registry) {
        return registry.timeLimiter("accountServiceTimeLimiter");
    }

    @Bean
    public Retry notificationServiceRetry(RetryRegistry registry) {
        return registry.retry("notificationServiceRetry");
    }
}