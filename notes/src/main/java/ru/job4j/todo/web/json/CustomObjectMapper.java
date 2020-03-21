package ru.job4j.todo.web.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class CustomObjectMapper extends ObjectMapper {
    private static final ObjectMapper INSTANCE_HANDLER = new CustomObjectMapper();

    private CustomObjectMapper() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        setDateFormat(dateFormat);
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static ObjectMapper getMapper() {
        return INSTANCE_HANDLER;
    }
}
