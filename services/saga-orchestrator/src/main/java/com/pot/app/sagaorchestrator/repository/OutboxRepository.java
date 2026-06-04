package com.pot.app.sagaorchestrator.repository;

import com.pot.app.sagaorchestrator.entity.Outbox;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends GeneralRepository<Outbox, UUID> {

    @Query(value = """
            SELECT o.* FROM outbox o
                        WHERE status = :status AND next_attempt_at <= CURRENT_TIMESTAMP
                        LIMIT :limit
                        FOR UPDATE SKIP LOCKED;
            """, nativeQuery = true)
    List<Outbox> findAllByStatus(@Param("status") String status, @Param("limit") Integer limit);

    @Modifying
    @Query(value = "DELETE FROM Outbox o WHERE o.id IN :ids")
    void deleteByIds(@Param("ids") Collection<UUID> ids);
}
