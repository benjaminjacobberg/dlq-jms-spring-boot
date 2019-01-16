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
