package com.pot.app.transactionservice.service;

import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse payment(PaymentRequest request);
}
