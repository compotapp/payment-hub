package com.pot.app.analyticsservice.service;

import com.pot.app.analyticsservice.entity.RawEvent;
import com.pot.app.analyticsservice.repository.RawEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RawEventRepository repository;

    @Override
    @Transactional
    public void save(RawEvent entity) {
        repository.save(entity);
    }
}
