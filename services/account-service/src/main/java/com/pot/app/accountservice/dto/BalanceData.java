package com.pot.app.accountservice.dto;

public record BalanceData(
        Long available,
        Long reserved
) {

    public static BalanceData zeroBalance() {
        return new BalanceData(0L, 0L);
    }
}
