package ru.job4j.vacancy.util;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.jsoup.HhRuJsoupProcessor;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.HabrCareerJsoupProcessor;
import ru.job4j.vacancy.jsoup.SqlRuJsoupProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Contains utility logic for jsoup working
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-14
 */
public class JsoupHelper {


    /**
     * should not instantiate
     */
    private JsoupHelper() {
    }

    // for more vacancy sites support in future
    public static final Map<String, JsoupProcessor> JSOUP_STRATEGIES = new HashMap<>() {
        private final JsoupProcessor defaultValue;

        {
            put("sql.ru", new SqlRuJsoupProcessor());
            put("hh.ru", new HhRuJsoupProcessor());
            put("moikrug.ru", new HabrCareerJsoupProcessor());

            defaultValue = get("sql.ru");
        }

        @Override
        public JsoupProcessor get(Object key) {
            return super.getOrDefault(key, defaultValue);
        }
    };

    // for more vacancy filters support in future
    public static final Map<String, Predicate<String>> VACANCY_FILTERS = new HashMap<>() {
        private final Predicate<String> defaultValue;

        {
            put("java", Filters::javaFilter);
            put(null, Filters.DEFAULT_FILTER);

            defaultValue = get("java");
        }

        @Override
        public Predicate<String> get(Object key) {
            return super.getOrDefault(key, defaultValue);
        }
    };

    /**
     * Prepares and returns the jsoup processor fitting the job context
     *
     * @param context job context
     * @return jsoup processor
     */
    public static JsoupProcessor specifyJsoupProcessor(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        JsoupProcessor jsoupProcessor = JSOUP_STRATEGIES.get(dataMap.getString("vacancy.site"));
        String searchKey = dataMap.getString("vacancy.filter");
        Predicate<String> vacancyFilter = VACANCY_FILTERS.get(searchKey);
        jsoupProcessor.submitSearchWord(searchKey);
        jsoupProcessor.submitSearchFilter(vacancyFilter);
        return jsoupProcessor;
    }

    public static class Filters {
        public static final Predicate<String> DEFAULT_FILTER = val -> true;

        public static boolean javaFilter(String title) {
            String tested = title.toLowerCase();
            boolean result = tested.contains("java");
            if (result) {
                // filter for java + javascript vacancies
                tested = tested.replaceAll("\\s+", "");
                tested = tested.replace("javascript", "");
            }
            return result && tested.contains("java");
        }
    }
}