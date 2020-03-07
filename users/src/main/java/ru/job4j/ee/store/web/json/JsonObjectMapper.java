package ru.job4j.ee.store.web.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

/**
 * Customized JSON mapper to serialize/deserialize entities
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class JsonObjectMapper extends ObjectMapper {
    private static final ObjectMapper INSTANCE_HANDLER = new JsonObjectMapper();

    private JsonObjectMapper() {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        setDateFormat(dateFormat);
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getJsonMapper() {
        return INSTANCE_HANDLER;
    }
}
