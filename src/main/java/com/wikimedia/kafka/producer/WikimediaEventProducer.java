package com.wikimedia.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;
import java.util.function.Consumer;

@Component
@Slf4j
public class WikimediaEventProducer implements ApplicationRunner {

    @Value("${topic.name}")
    private String topic;

    @Value("${wikimedia.recentchange.uri}")
    private String changesUrl;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting producer...");
        eventProducer();
    }

    private void eventProducer() {
        WebClient client = WebClient.create(changesUrl);
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<>() {};

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri(changesUrl)
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(this::publishMessage, this::onError, this::onComplete);
        /*eventStream.subscribe(
                content -> log.info("Time: {} - event: name[{}], id [{}], content[{}] ",
                        LocalTime.now(), content.event(), content.id(), content.data()),
                error -> log.error("Error receiving SSE: {}", error),
                () -> log.info("Completed!!!"));*/
    }

    private void publishMessage(ServerSentEvent<String> content) {
        //TODO: set message expiry and partition
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, content.id(), content.data());
        kafkaTemplate.send(record);
    }

    private void onError(Throwable error) {
        log.error("Error receiving SSE: {}", error.getMessage(), error);
    }

    private void onComplete() {
        log.info("Completed publishing SSE");
    }

}
