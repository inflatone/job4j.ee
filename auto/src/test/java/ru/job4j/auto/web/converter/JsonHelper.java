package ru.job4j.auto.web.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonHelper {
    private final ObjectMapper mapper;

    /**
     * Composes an object of the given class, fills its fields with JSON data presented as string
     *
     * @param json  json line
     * @param clazz entity class
     * @return entity
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Composes an entity sequence of the given class, fills fields of its elements with JSON data presented as string
     *
     * @param json  json line
     * @param clazz entity class
     * @return entities
     */
    public <T> List<T> listFromJson(String json, Class<T> clazz) {
        var jsonReader = mapper.readerFor(clazz);
        try {
            return jsonReader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON: " + e.getMessage(), e);
        }
    }

    public <K, V> Map<K, V> mapFromJson(String json, TypeReference<Map<K, V>> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid read from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the given entity as JSON string
     *
     * @param obj entity
     * @return JSON string
     */
    public <T> String asJson(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + '\'');
        }
    }

    public <T> String asJsonAdditional(T obj, Map<String, Object> additional) {
        Map<String, Object> objAsPropertyMap = mapper.convertValue(obj, new TypeReference<>() {
        });
        objAsPropertyMap.putAll(additional);
        return asJson(objAsPropertyMap);
    }

    public <T> String asJsonAdditional(T obj, String key, Object value) {
        return asJsonAdditional(obj, Map.of(key, value));
    }


}
