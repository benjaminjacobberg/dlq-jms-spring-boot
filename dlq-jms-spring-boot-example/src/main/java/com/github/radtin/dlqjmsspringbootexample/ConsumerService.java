package com.github.radtin.dlqjmsspringbootexample;

import org.springframework.messaging.Message;

public interface ConsumerService {
    void get(Message message);
}
