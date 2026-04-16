package com.pot.app.accountservice.service;

import com.pot.app.accountservice.entity.Account;
import com.pot.app.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    @Transactional
    public Optional<Account> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Account update(Account account) {
        return repository.save(account);
    }
}
