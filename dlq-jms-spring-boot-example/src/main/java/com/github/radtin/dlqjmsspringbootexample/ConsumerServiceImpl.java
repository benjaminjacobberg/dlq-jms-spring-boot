package com.github.radtin.dlqjmsspringbootexample;

import com.github.radtin.dlqjmsspringboot.Dlq;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ConsumerServiceImpl implements ConsumerService {
    @JmsListener(destination = "${queue}")
    @Dlq(destination = "${dlq}", retry = 3, exponentialBackoff = 1000)
    @Override
    public void get(Message message) {
        throw new RuntimeException("Throwing exception to send message to the DLQ.");
    }
}
