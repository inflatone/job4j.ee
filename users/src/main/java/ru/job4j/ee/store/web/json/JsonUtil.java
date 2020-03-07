package ru.job4j.ee.store.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;

import static ru.job4j.ee.store.web.json.JsonObjectMapper.getJsonMapper;

/**
 * Contains utility methods to work with JSON serialization/deserialization
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class JsonUtil {
    /**
     * Composes an object of the given class, fills its fields by JSON data extracted from the given reader
     *
     * @param reader reader
     * @param clazz  entity class
     * @return entity
     */
    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        try {
            return getJsonMapper().readValue(reader, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON", e);
        }
    }

    public static <T> List<T> listFromJson(Reader reader, Class<T> clazz) {
        var jsonReader = getJsonMapper().readerFor(clazz);
        try {
            return jsonReader.<T>readValues(reader).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON", e);
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
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + '\'', e);
        }
    }

    public static <T> String asJsonAdditional(T obj, Map<String, Object> additional) {
        Map<String, Object> objAsPropertyMap = getJsonMapper().convertValue(obj, new TypeReference<>() {});
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
        return asJson(new SimpleImmutableEntry<>(key, value));
    }

    /**
     * Writes the given key-value pair to response as JSON
     *
     * @param response response
     * @param key      key
     * @param value    value
     */
    public static <K, V> void asJsonToResponse(HttpServletResponse response, K key, V value) throws IOException {
        var json = asJson(key, value);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    /**
     * Writes the given entity to response as JSON
     *
     * @param response response
     * @param obj      entity
     */
    public static <T> void asJsonToResponse(HttpServletResponse response, T obj) throws IOException {
        var json = asJson(obj);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    /**
     * Writes the given error message to response as JSON
     *
     * @param response response
     * @param message  error message
     */
    public static void errorAsJsonToResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        asJsonToResponse(response, "error", message);
    }
}