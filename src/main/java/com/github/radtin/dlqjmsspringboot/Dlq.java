package com.github.radtin.dlqjmsspringboot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retry executing your method "retry" amount of times with an exponential backoff of "exponentialBackoff".  If the max
 * amount of retries have been met, send message payload to the dead letter queue "destination".
 *
 * @author Benjamin Jacob Berg (benjaminjacobberg@gmail.com) - A developer with a dream to one day make life easier with AOP.
 * @version 0.0.1
 * @implNote Annotated method must use {@link org.springframework.messaging.Message} as the first parameter.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dlq {
    String destination();

    int retry() default 1;

    int exponentialBackoff() default 0;
}
