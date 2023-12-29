package com.wikimedia.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestConsumer {

    @KafkaListener(topics = "${topic.name}", groupId = "group-id")
    public void consume(ConsumerRecord<String, String> record) {
        String message = record.value();
        log.info("Consumer Message: {}", message);
    }

}
