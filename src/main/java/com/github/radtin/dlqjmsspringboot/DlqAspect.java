package com.github.radtin.dlqjmsspringboot;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsOperations;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Catches exceptions thrown by the annotated method and retries the process for "x" amount of times with "y" as the
 * exponential time in milliseconds.  When the max amount of retries has been met, send the message payload to the
 * Dead Letter Queue.
 *
 * @author Benjamin Jacob Berg (benjaminjacobberg@gmail.com) - A developer with a dream to one day make life easier with AOP.
 * @version 0.0.1
 * @implNote Annotated method must use {@link org.springframework.messaging.Message} as the first parameter.
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class DlqAspect {
    @NonNull private Environment   environment;
    @NonNull private JmsOperations jmsOperations;

    /**
     * Extracts the required information from the {@link Dlq} annotation and it's annotated method.  Uses that information
     * if an exception is caught to retry executing the annotated method until the max amount of retries has been met and
     * then will send the message payload to the Dead Letter Queue.
     *
     * @param joinPoint Exposes the proceed(..) method
     * @return Next advice or target method invocation
     * @throws Throwable {@link Throwable} will be thrown if the method can not be found
     */
    @Around("@annotation(Dlq)")
    public Object dlq(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getMethod().getName(), signature.getMethod().getParameterTypes());

        Dlq dlqAnnotation = method.getAnnotation(Dlq.class);
        String destination = dlqAnnotation.destination().contains("${") ? environment.getProperty(dlqAnnotation.destination().replace("${", "").replace("}", ""))
                                                                        : dlqAnnotation.destination();

        int retry = dlqAnnotation.retry();
        int exponentialBackoff = dlqAnnotation.exponentialBackoff();

        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
        } catch (Exception e) {
            log.error("Exception has been caught with the message of ({}).  Executing retry with exponential backoff.", e.getMessage());
            retryWithExponentialBackoff(joinPoint, destination, retry, exponentialBackoff);
        }

        return proceed;
    }

    /**
     * Retry executing the method with exponential backoff.  Send message to DLQ if max retries have been met.
     *
     * @param joinPoint          Exposes the proceed(..) method
     * @param destination        Dead Letter Queue
     * @param retry              Times to retry executing the method
     * @param exponentialBackoff The starting time in milliseconds for exponential backoff
     * @throws Throwable {@link Throwable} will be thrown if the method can not be found
     */
    private void retryWithExponentialBackoff(ProceedingJoinPoint joinPoint, String destination, int retry, int exponentialBackoff) throws Throwable {
        int i = 1;
        boolean failed = true;
        while (failed) {
            if (i > retry) {
                break;
            }
            if (exponentialBackoff != 0) {
                Thread.sleep(exponentialBackoff);
                exponentialBackoff += exponentialBackoff * 2;
            }
            try {
                joinPoint.proceed();
                failed = false;
                log.info("Retrying function lead to a successful execution.  Retries: ({})", i);
            } catch (Exception e1) {
                log.error("Error while trying to execute function. - Exception Message: ({}) Current Retry Count: ({})", e1.getMessage(), i);
                i++;
            }
        }
        if (failed) {
            log.error("Max retries reached. Sending message to the dead letter queue ({}).", destination);
            jmsOperations.convertAndSend(destination, ((Message) joinPoint.getArgs()[0]).getPayload()); // TODO - Make argument more dynamic and flexible for different types.
        }
    }
}