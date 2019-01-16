# dlq-jms-spring-boot
Annotation for sending messages to the Dead Letter Queue if there is an exception.

## Sample
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    @NonNull private JmsOperations jmsOperations;

    @JmsListener(destination = "${queues.origin.queue}")
    @Dlq(destination = "${queues.origin.dlq}", retry = 3, exponentialBackoff = 1000)
    @Override
    public void processMessage(Message message) {
        // Throw an exception here to test the DLQ annotation.
        throw new RuntimeException("TODO - Method has not been implemented.");
    }
}

```


Depending on your project, you may need to setup component scanning to find DlqAspect.java.  
```java
@ComponentScan(basePackages = "com.github.radtin")
```
