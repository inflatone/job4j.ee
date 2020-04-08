package ru.job4j.auto.web.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import javax.annotation.PostConstruct;

public class JacksonObjectMapper extends ObjectMapper {
    @PostConstruct
    public void setUp() {
        var hibernate5Module = new Hibernate5Module()
                .configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, false)
                .configure(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        registerModule(hibernate5Module);

        findAndRegisterModules();

        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
}
