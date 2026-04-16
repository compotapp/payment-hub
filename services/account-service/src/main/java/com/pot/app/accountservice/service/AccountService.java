package com.pot.app.accountservice.service;

import com.pot.app.accountservice.entity.Account;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findByUserId(String userId);

    Account update(Account account);
}
