package com.pot.app.transactionservice.dto;

public record PushRequest(String userId, String title, String body, String transactionId) {}
