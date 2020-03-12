package ru.job4j.vacancy.jsoup;

import ru.job4j.vacancy.sql.SQLProcessor;
import ru.job4j.vacancy.sql.SQLUtil;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static ru.job4j.vacancy.job.VacancyCollectorJob.VACANCY_CITY;
import static ru.job4j.vacancy.job.VacancyCollectorJob.VACANCY_KEYWORD;
import static ru.job4j.vacancy.model.SourceTitle.*;

/**
 * Contains utility logic for jsoup working
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-14
 */
public class ParserUtil {
    private ParserUtil() {
        throw new IllegalStateException("should not instantiate");
    }

    private static final Map<String, Supplier<JsoupProcessor>> PROCESSORS = Map.of(
            sql_ru.title(), SqlRuJsoupProcessor::new,
            hh_ru.title(), HhRuJsoupProcessor::new,
            habr_com.title(), HabrCareerJsoupProcessor::new
    );

    /**
     * Retrieves the property value associated with the given key, checking its availability
     *
     * @param propertyRetriever property producer
     * @param propertyName      property name to compose error message
     * @return property value
     * @throws IllegalArgumentException if property value's missed
     */
    public static <T> T necessarilyGet(Supplier<T> propertyRetriever, String propertyName) {
        var result = propertyRetriever.get();
        if (result == null) {
            throw new IllegalArgumentException(format("'%s' is missing or incorrect", propertyName));
        }
        return result;
    }

    /**
     * Prepares and returns the jsoup processor fitting the property context
     *
     * @param properties job properties
     * @return jsoup processor
     */
    public static JsoupProcessor createJsoupProcessor(Map<?, ?> properties) {
        String source = (String) properties.get("vacancy.source");
        return necessarilyGet(() -> PROCESSORS.get(source == null ? "sql.ru" : source),
                "vacancy.source").get();
    }

    /**
     * Prepares and returns the sql processor fitting the property context
     *
     * @param properties job properties
     * @return sql processor
     */
    public static SQLProcessor createSQLProcessor(Map<?, ?> properties) {
        try {
            var factory = SQLUtil.getConnectionFactory(properties);
            return new SQLProcessor(factory);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver not found", e);
        }
    }

    public static ParseParameters buildParseParameters(Map<String, ?> dataMap, ZonedDateTime startDateTime) {
        var keyword = necessarilyGet(() -> (String) dataMap.get(VACANCY_KEYWORD), VACANCY_KEYWORD).toLowerCase();
        var city = (String) dataMap.get(VACANCY_CITY);
        return ParseParameters.of(keyword, city, startDateTime);
    }
}