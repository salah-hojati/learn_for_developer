To use Kafka with Spring Boot in a standalone (single-node) setup, the fastest path is to run Kafka via Docker or locally, then add the `spring-kafka` dependency. Spring Boot's auto-configuration handles most wiring, letting you focus on sending and receiving messages.

### 🐳 Running Standalone Kafka

You don't need a full cluster for development. The modern approach uses **KRaft mode**, which removes the old ZooKeeper dependency .

**Option A: Docker (Recommended)**
This is the cleanest method. Run the official image or a Confluent image:
```bash
docker run -p 9092:9092 apache/kafka:4.3.1
```
*(Note: If using `confluentinc/cp-kafka`, you must configure KRaft environment variables as shown in the Docker Compose example .)*

**Option B: Local Scripts**
If you downloaded the Kafka binaries, you must first format the storage with a cluster ID, then start the server :
```bash
bin/kafka-storage.sh random-uuid
bin/kafka-storage.sh format --standalone -t <YOUR_UUID> -c config/server.properties
bin/kafka-server-start.sh config/server.properties
```

### 🛠️ Spring Boot Integration

**1. Dependency**
Add the Spring Kafka starter to your `pom.xml` (Spring Boot manages the version):
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

**2. Configuration (`application.properties`)**
Point Spring to your local broker. Setting `auto-offset-reset=earliest` ensures your consumer reads messages sent before it started .
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=my-group
spring.kafka.consumer.auto-offset-reset=earliest
```

**3. Sending Messages (Producer)**
Spring Boot auto-configures a `KafkaTemplate`. Inject it and call `send()` :
```java
@Service
public class MyProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MyProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message) {
        kafkaTemplate.send("my-topic", message);
    }
}
```

**4. Receiving Messages (Consumer)**
Annotate a method with `@KafkaListener`. It will automatically poll the topic :
```java
@Component
public class MyConsumer {
    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
```

**5. Auto-Create Topic (Optional)**
To avoid "topic not found" errors, define a `NewTopic` bean. Spring will create it on startup if it doesn't exist :
```java
@Bean
public NewTopic myTopic() {
    return TopicBuilder.name("my-topic").partitions(1).replicas(1).build();
}
```

---
**One small note:** If you later switch from `String` to JSON objects, you'll need to configure `JsonSerializer` and `JsonDeserializer` in your properties or via a custom `ProducerFactory`/`ConsumerFactory` .