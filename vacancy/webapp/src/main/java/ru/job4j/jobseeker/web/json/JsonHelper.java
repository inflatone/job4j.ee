package ru.job4j.jobseeker.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static ru.job4j.jobseeker.web.json.JsonObjectMapper.getJsonMapper;

/**
 * Contains utility methods to work with JSON serialization/deserialization
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class JsonHelper {
    private JsonHelper() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Composes an object of the given class, fills its fields with JSON data extracted from the given reader
     *
     * @param reader reader
     * @param clazz  entity class
     * @return entity
     */
    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        try {
            return getJsonMapper().readValue(reader, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Composes an object of the given class, fills its fields with JSON data presented as string
     *
     * @param json  json line
     * @param clazz entity class
     * @return entity
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return getJsonMapper().readValue(json, clazz);
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
    public static <T> List<T> listFromJson(String json, Class<T> clazz) {
        var jsonReader = getJsonMapper().readerFor(clazz);
        try {
            return jsonReader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the given entity as JSON string
     *
     * @param obj entity
     * @return JSON string
     */
    public static <T> String asJson(T obj) {
        try {
            return getJsonMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + '\'' + '\n' + e.getMessage(), e);
        }
    }

    public static <T> String asJsonAdditional(T obj, Map<String, Object> additional) {
        Map<String, Object> objAsPropertyMap = getJsonMapper().convertValue(obj, new TypeReference<>() {
        });
        objAsPropertyMap.putAll(additional);
        return asJson(objAsPropertyMap);
    }

    public static <T> String asJsonAdditional(T obj, String key, Object value) {
        return asJsonAdditional(obj, Map.of(key, value));
    }

    /**
     * Returns the given key-value pair as JSON string
     *
     * @param key   key
     * @param value value
     * @return JSON string
     */
    public static <K, V> String asJson(K key, V value) {
        return asJson(entry(key, value));
    }

}