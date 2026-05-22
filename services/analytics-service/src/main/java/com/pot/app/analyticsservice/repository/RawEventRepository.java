package com.pot.app.analyticsservice.repository;

import com.pot.app.analyticsservice.entity.RawEvent;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RawEventRepository extends GeneralRepository<RawEvent, UUID> {
}
