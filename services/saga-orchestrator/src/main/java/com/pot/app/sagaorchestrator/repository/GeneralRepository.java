package com.pot.app.sagaorchestrator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@NoRepositoryBean
@Transactional(propagation = MANDATORY)
public interface GeneralRepository<T, ID> extends JpaRepository<T, ID> {
}
