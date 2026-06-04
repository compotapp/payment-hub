package com.pot.app.sagaorchestrator.schedule;


import com.pot.app.sagaorchestrator.service.OutboxWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxWorker worker;

    // Запускается каждые 5 секунд
    @Scheduled(fixedDelay = 20000)
    public void run() {
        worker.processBath();
    }
}
