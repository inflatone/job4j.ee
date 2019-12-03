package ru.job4j.todo.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.Reader;

import static ru.job4j.todo.web.json.CustomObjectMapper.getMapper;

public class JsonUtil {
    public static <T> T readValue(Reader reader, Class<T> clazz) {

        try {
            return getMapper().readValue(reader, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON");
        }
    }

    public static <T> String writeValue(T obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + '\'');
        }
    }
}
