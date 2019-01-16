package com.github.radtin.dlqjmsspringbootexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableJms
@ComponentScan(basePackages = "com.github.radtin")
public class DlqJmsSpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlqJmsSpringBootExampleApplication.class, args);
    }

}

