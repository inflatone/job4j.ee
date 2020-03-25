package ru.job4j.jobseeker.web.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
        findAndRegisterModules();
        configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getJsonMapper() {
        return INSTANCE_HANDLER;
    }
}
