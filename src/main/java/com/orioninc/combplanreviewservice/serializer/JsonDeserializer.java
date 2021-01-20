package com.orioninc.combplanreviewservice.serializer;

import com.google.gson.Gson;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {
    private final Gson gson = new Gson();
    private Class<T> deserializedClass;

    public JsonDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
        //init();
    }

    public JsonDeserializer() {
    }

    @Override
    @SuppressWarnings("uncheked")
    public void configure(Map<String, ?> configs, boolean isKey) {
        if(deserializedClass == null){
            deserializedClass = (Class<T>)configs.get("serializedClass");
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if(data == null){
            return null;
        }
        return gson.fromJson(new String(data), deserializedClass);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {

    }
}
