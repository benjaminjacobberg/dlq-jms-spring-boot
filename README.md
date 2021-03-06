# dlq-jms-spring-boot-parent

## Modules
* dlq-jms-spring-boot
* dlq-jms-spring-boot-example

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

# dlq-jms-spring-boot-example

In this example, I'm using IBM MQ to place a message on the queue and having the Spring Boot app grab the message and 
throw an exception causing the message to go to the DLQ.

## Run
* Start IBM MQ
```
$ docker-compose up
```

* Run the JAR
```
$ mvn spring-boot:run
```

* Open your web browser and navigate to [IBM MQ Web Console](https://:9443/ibmmq/console) and use the following 
* credentials to login.
    * User: admin
    * Password: passw0rd

* Put a message on the queue 'DEV.QUEUE.1'.

Now you can watch the DLQ annotation do it's magic.
