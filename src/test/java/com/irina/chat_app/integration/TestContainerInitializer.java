package com.irina.chat_app.integration;

import com.redis.testcontainers.RedisContainer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.HashMap;
import java.util.Map;

public class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    private static final PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("chat_app")
            .withUsername("chat_app")
            .withPassword("chat_app");

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "chat_app")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "chat_app")
            .withCommand("mongod", "--bind_ip_all", "--port", "27017");

    @Container
    private static final RedisContainer redisContainer = new RedisContainer("7.0.0");

    @Container
    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management")
            .withEnv("RABBITMQ_DEFAULT_USER", "chat_app")
            .withEnv("RABBITMQ_DEFAULT_PASS", "chat_app")
            .withCommand("sh", "-c",
                    "rabbitmq-plugins enable rabbitmq_web_stomp && rabbitmq-server");


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        postgresDBContainer.start();
        mongoDBContainer.start();
        redisContainer.start();
        rabbitMQContainer.start();


        Map<String, Object> testProperties = new HashMap<>();
        testProperties.put("spring.datasource.url", postgresDBContainer.getJdbcUrl());
        testProperties.put("spring.datasource.username", postgresDBContainer.getUsername());
        testProperties.put("spring.datasource.password", postgresDBContainer.getPassword());
        testProperties.put("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
        testProperties.put("spring.redis.host", redisContainer.getHost());
        testProperties.put("spring.redis.port", redisContainer.getFirstMappedPort());
        testProperties.put("spring.rabbitmq.host", rabbitMQContainer.getHost());
        testProperties.put("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort());


        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("testcontainers", testProperties));
    }
}