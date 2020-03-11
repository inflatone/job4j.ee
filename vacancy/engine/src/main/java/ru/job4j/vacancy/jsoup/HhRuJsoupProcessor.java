package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static java.lang.String.format;

/**
 * Represents program strategy of sql.ru parsing
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-14
 */
public class HhRuJsoupProcessor extends AbstractJsoupProcessor {
    private static final String URL_TEMPLATE
            = "http://hh.ru/search/vacancy?text=%s&page=%d&items_on_page=100&order_by=publication_time";

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));

    @Override
    boolean anyMorePages(int i) {
        // hh.ru submits only 0-19 pages (in case "100 vacancies per page"), then returns 404 http status
        return i < 20;
    }

    @Override
    String buildPageLink(int page) {
        return format(URL_TEMPLATE, searchWord, page - 1); // hh.ru starts with 0 page
    }

    @Override
    Elements getAllVacancyRowsOnPage(Document doc) {
        Elements vacancies = doc.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");
        Elements premiumVacancies = doc.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy vacancy-serp__vacancy_premium");
        vacancies.addAll(premiumVacancies);
        return vacancies;
    }

    @Override
    String grabTitle(Element row) {
        return getDataQaText(row, "vacancy-serp__vacancy-title");
    }

    @Override
    String grabCity(Element row) {
        return getDataQaText(row, "vacancy-serp__vacancy-address");
    }

    @Override
    String grabCompany(Element row) {
        return getDataQaText(row, "vacancy-serp__vacancy-employer");
    }

    @Override
    String grabDateTime(Element row) {
        return getDataQaText(row, "vacancy-serp__vacancy-date");
    }

    @Override
    LocalDateTime parseDateTime(String dateLine) {
        return LocalDateTime.of(
                dateLine.isEmpty() ? LocalDate.now() : parseDate(dateLine),
                LocalTime.MIN
        );
    }

    private LocalDate parseDate(String dateLine) {
        LocalDate today = LocalDate.now();
        TemporalAccessor dayAndMonth = FORMATTER.parse(dateLine);
        LocalDate result = LocalDate.of(today.getYear(),
                dayAndMonth.get(ChronoField.MONTH_OF_YEAR), dayAndMonth.get(ChronoField.DAY_OF_MONTH));
        // to prevent year mistakes(on 5 JAN "27 DEC" was already last year)
        return result.isAfter(today) ? result.minusYears(1) : result;
    }

    @Override
    String grabLink(Element row) {
        return row.select("a").first().attr("href");
    }

    @Override
    String grabDescription(Element row) {
        return getDataQaText(row, "vacancy-serp__vacancy_snippet_responsibility")
                + '\n'
                + getDataQaText(row, "vacancy-serp__vacancy_snippet_requirement");
    }

    private String getDataQaText(Element e, String attributeValue) {
        return e.getElementsByAttributeValue("data-qa", attributeValue).text();
    }
}