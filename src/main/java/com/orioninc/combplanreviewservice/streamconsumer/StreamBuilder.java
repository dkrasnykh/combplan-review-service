package com.orioninc.combplanreviewservice.streamconsumer;

import com.orioninc.combplanreviewservice.dto.RequestDto;
import com.orioninc.combplanreviewservice.dto.RequestStatus;
import com.orioninc.combplanreviewservice.dto.ReviewDto;
import com.orioninc.combplanreviewservice.dto.Role;
import com.orioninc.combplanreviewservice.dto.UserDto;
import com.orioninc.combplanreviewservice.serializer.JsonDeserializer;
import com.orioninc.combplanreviewservice.serializer.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Service
public class StreamBuilder {

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "review-service-id");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return props;
    }

    @PostConstruct
    public void start() {
        Serde<Long> longSerde = Serdes.Long();

        JsonSerializer<UserDto> userJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<UserDto> userJsonDeserializer = new JsonDeserializer<>(UserDto.class);
        Serde<UserDto> userSerde = Serdes.serdeFrom(userJsonSerializer, userJsonDeserializer);

        JsonSerializer<RequestDto> requestJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<RequestDto> requestJsonDeserializer = new JsonDeserializer<>(RequestDto.class);
        Serde<RequestDto> requestSerde = Serdes.serdeFrom(requestJsonSerializer, requestJsonDeserializer);

        JsonSerializer<ReviewDto> reviewJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<ReviewDto> reviewJsonDeserializer = new JsonDeserializer<>(ReviewDto.class);
        Serde<ReviewDto> reviewSerde = Serdes.serdeFrom(reviewJsonSerializer, reviewJsonDeserializer);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<Long, RequestDto> requestKStream = streamsBuilder
                .stream("application-service.request", Consumed.with(longSerde, requestSerde))
                .filter((id, request) -> (request.getStatus().equals(RequestStatus.COMPLETED)));

        KStream<Long, ReviewDto> reviewKStream = requestKStream.mapValues(request -> ReviewDto.builder(request).build());
        reviewKStream.to("notification-topic", Produced.with(longSerde, reviewSerde));

        ForeachAction<Long, UserDto> userDtoForeachAction = (key, user) -> UserStore.addUser(user);
        streamsBuilder.stream("user-service.user", Consumed.with(longSerde, userSerde))
                .filter((key, user) -> (user.getRoles().contains(Role.REVIEWER)))
                .foreach(userDtoForeachAction);

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), getProperties());
        streams.start();
    }
}
