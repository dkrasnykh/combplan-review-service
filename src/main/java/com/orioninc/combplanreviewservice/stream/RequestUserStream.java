package com.orioninc.combplanreviewservice.stream;

import com.orioninc.combplanreviewservice.config.AppConfig;
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
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Service
public class RequestUserStream {

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

        final GlobalKTable<Long, UserDto> users = streamsBuilder
                .globalTable(AppConfig.USER_SERVICE_TOPIC, Materialized.<Long, UserDto, KeyValueStore<Bytes, byte[]>>as(AppConfig.USER_STORE)
                        .withKeySerde(longSerde)
                        .withValueSerde(userSerde));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), getProperties());
        streams.start();

        UserDto reviewer = null;

        while (true) {
            if (streams.state().equals(KafkaStreams.State.RUNNING)) {
                ReadOnlyKeyValueStore view = streams.store(AppConfig.USER_STORE, QueryableStoreTypes.keyValueStore());
                KeyValueIterator<Long, UserDto> iterator = view.all();
                while (iterator.hasNext()) {
                    reviewer = iterator.next().value;
                    if (reviewer.getRoles().contains(Role.REVIEWER)) {
                        break;
                    }
                }
                break;
            }
        }

        streams.close();

        streamsBuilder = new StreamsBuilder();

        KStream<Long, RequestDto> requestKStream = streamsBuilder
                .stream(AppConfig.APPLICATION_SERVICE_TOPIC, Consumed.with(longSerde, requestSerde))
                .filter((id, request) -> (request.getStatus().equals(RequestStatus.COMPLETED)));

        UserDto finalReviewer = reviewer;
        KStream<Long, ReviewDto> reviewKStream = requestKStream.mapValues(request -> new ReviewDto(request, finalReviewer));
        reviewKStream.to(AppConfig.NOTIFICATION_TOPIC, Produced.with(longSerde, reviewSerde));

        streams = new KafkaStreams(streamsBuilder.build(), getProperties());
        streams.start();
    }
}
