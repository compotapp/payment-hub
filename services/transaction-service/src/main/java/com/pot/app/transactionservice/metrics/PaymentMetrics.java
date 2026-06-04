package com.pot.app.transactionservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class PaymentMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter paymentSuccessCounter;
    private final Counter paymentFailureCounter;
    private final Timer paymentProcessingTimer;
    
    public PaymentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.paymentSuccessCounter = Counter.builder("payment.success.total")
            .description("Total successful payments")
            .register(meterRegistry);
        
        this.paymentFailureCounter = Counter.builder("payment.failure.total")
            .description("Total failed payments")
            .register(meterRegistry);
        
        this.paymentProcessingTimer = Timer.builder("payment.processing.duration")
            .description("Payment processing time")
            .register(meterRegistry);
    }
    
    public void recordSuccess() {
        paymentSuccessCounter.increment();
    }
    
    public void recordFailure() {
        paymentFailureCounter.increment();
    }
    
    public <T> T recordProcessingTime(Supplier<T> supplier) {
        return paymentProcessingTimer.record(supplier);
    }
}