package com.wikimedia.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Slf4j
public class TestProducer {

    @Value("${topic.name}")
    private String topic;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //@Scheduled(cron = "0 */1 * ? * *")
    public void sendMessage() {
        UUID key = UUID.randomUUID();
        String message = "Message " + key;
        log.info("Sending Message: {}", message);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key.toString(), message);
        record.headers().add("message-id", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }

}
