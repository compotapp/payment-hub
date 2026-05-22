package com.pot.app.analyticsservice.service;

import com.pot.app.analyticsservice.entity.RawEvent;

public interface AnalyticsService {

    void save(RawEvent entity);
}
