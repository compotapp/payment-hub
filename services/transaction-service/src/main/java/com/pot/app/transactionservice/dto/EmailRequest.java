package com.pot.app.transactionservice.dto;

public record EmailRequest(String to, String subject, String body, String transactionId) {}
