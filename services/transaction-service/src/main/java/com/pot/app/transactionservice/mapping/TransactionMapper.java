package com.pot.app.transactionservice.mapping;

import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

import static com.pot.app.transactionservice.constans.TransactionType.PAYMENT;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        imports = {UUID.class}// для статических методов/конструкторов
)
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", expression = "java(generatedTransactionId())")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", constant = PAYMENT)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(PaymentRequest request);

    default String generatedTransactionId() {
        return UUID.randomUUID().toString();
    }
}
