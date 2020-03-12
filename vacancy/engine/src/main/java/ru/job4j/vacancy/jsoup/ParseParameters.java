package ru.job4j.vacancy.jsoup;

import lombok.Getter;
import one.util.streamex.StreamEx;
import ru.job4j.vacancy.model.SourceTitle;
import ru.job4j.vacancy.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static ru.job4j.vacancy.util.TimeUtil.asLine;

/**
 * Vacancy parser parameter class
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-15
 */
@Getter
public class ParseParameters {
    private final String keyword;

    private final String city;

    private final String searchQuery;


    private final Predicate<String> titleFilter;

    private final ZonedDateTime start;

    private final ZonedDateTime finish;

    // gives an opportunity to add multisource scan support in future
    private final Set<SourceTitle> sources;

    public ParseParameters(String keyword, String city, Predicate<String> titleFilter, ZonedDateTime start, ZonedDateTime finish, Set<SourceTitle> sources) {
        this.keyword = keyword.toLowerCase();
        this.city = city;
        this.searchQuery = city == null ? keyword : keyword + '+' + city;

        this.titleFilter = titleFilter;
        this.start = start;
        this.finish = finish;
        this.sources = sources;
    }

    public boolean isReachLimit(ZonedDateTime dateTime) {
        return dateTime.isBefore(start);
    }

    public boolean isInRange(ZonedDateTime dateTime) {
        return finish == null || !dateTime.isAfter(finish);
    }

    public boolean isValidTitle(String title) {
        return titleFilter == null || titleFilter.test(title);
    }

    public String getDateRangeAsLine() {
        return String.format("[%s — %s]", asLine(start),
                asLine(finish != null ? finish : TimeUtil.now()));
    }

    public static ParseParameters of(String keyword, String city, ZonedDateTime start, ZonedDateTime finish, Set<SourceTitle> sources) {
        Predicate<String> titleFilter = Filters.createFilter(keyword);
        return new ParseParameters(keyword, city, titleFilter, start, finish, sources);
    }

    public static ParseParameters of(String keyword, String city, ZonedDateTime start, ZonedDateTime finish) {
        return of(keyword, city, start, finish, Set.of());
    }

    public static ParseParameters of(Set<SourceTitle> sources, String keyword, String city, ZonedDateTime start) {
        return of(keyword, city, start, null, sources);
    }


    public static ParseParameters of(String keyword, String city, ZonedDateTime start) {
        return of(keyword, city, start, null);
    }

    public static ParseParameters of(String keyword, ZonedDateTime start) {
        return of(keyword, null, start);
    }

    private static class Filters {
        public static final Map<String, Predicate<String>> VACANCY_FILTERS = new HashMap<>();

        static {
            // for more vacancy filters support in future
            VACANCY_FILTERS.put("java", Filters::javaFilter);
        }

        public static Predicate<String> createFilter(String keyword) {
            var filter = VACANCY_FILTERS.get(keyword);
            return filter != null ? filter : defaultFilter(keyword);
        }

        private static boolean javaFilter(String title) {
            String tested = title.toLowerCase();
            boolean result = tested.contains("java");
            if (result) {
                // filter for java + javascript vacancies
                tested = tested.replaceAll("\\s+", "");
                tested = tested.replace("javascript", "");
            }
            return result && tested.contains("java");
        }

        private static Predicate<String> defaultFilter(String keyword) {
            var defaultMatcher = Pattern.compile(Filters.getWordsRegex(keyword), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            return title -> defaultMatcher.matcher(title).find();

        }

        private static String getWordsRegex(String keyword) {
            return String.format("(?=\\b%s\\b)", StreamEx.of(keyword.split(" ")).map(Pattern::quote).joining("|"));
        }
    }
}
