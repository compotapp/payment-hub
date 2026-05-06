//package com.pot.app.notificationservice.hendle;
//
//import com.pot.app.consumertest.exception.NonRetryableException;
//import com.pot.app.consumertest.exception.RetryableException;
//import com.pot.app.core.ProductCreatedEvent;
//import com.pot.app.core.ProductDeletedEvent;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaHandler;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
//
//@Component
//@Slf4j
//@KafkaListener(topics = "test-app-1-topic-1")//Event для всех методов
//public class ProductEventHandler {
//
//
//    @KafkaHandler//Будет слушать event по типу аргумента
//    public void handle(ProductCreatedEvent event) {
//        log.info("Received event: {}", event.getTitle());
//        try {
//            //TODO логика
//        } catch (ResourceAccessException e) {
//            throw new RetryableException("retry");
//        } catch (HttpServerErrorException e) {
//            throw new NonRetryableException("non retry");
//        } catch (Exception e) {
//            throw new NonRetryableException("non retry");
//        }
//    }
//
//    //@KafkaListener(topics = "test-app-1-topic-1")//Event для этого метода
//    @KafkaHandler//Будет слушать event по типу аргумента
//    public void handle(ProductDeletedEvent event) {
//
//    }
//}
