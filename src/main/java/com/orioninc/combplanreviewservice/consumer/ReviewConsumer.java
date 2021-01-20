package com.orioninc.combplanreviewservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orioninc.combplanreviewservice.dto.RequestDto;
import com.orioninc.combplanreviewservice.dto.ReviewDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ReviewConsumer {
    private final ObjectMapper mapper;

    @Autowired
    public ReviewConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void consume() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable consumerThread = getConsumerThread(getProperties());
        executorService.submit(consumerThread);
    }

    private Runnable getConsumerThread(Properties properties) {
        return () -> {
            Consumer<Long, String> consumer = null;
            try {
                consumer = new KafkaConsumer<>(properties);
                consumer.subscribe(Collections.singletonList("notification-topic"));
                while (true) {
                    ConsumerRecords<Long, String> records = consumer.poll(Duration.ofMillis(5000));
                    for (ConsumerRecord<Long, String> record : records) {
                        String value = record.value();
                        log.info("=> consumed {}", value);
                        ReviewDto reviewDto = readValue(value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        };
    }

    private ReviewDto readValue(String value) {
        try {
            return mapper.readValue(value, ReviewDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to ReviewDto failed: " + value);
        }
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "server.broadcast");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return props;
    }
}
