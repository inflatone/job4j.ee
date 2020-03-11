package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Represents program strategy of moikrug.ru parsing
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-15
 */
public class HabrCareerJsoupProcessor extends AbstractJsoupProcessor {
    private static final String SITE_NAME = "https://moikrug.ru";
    private static final String URL_TEMPLATE = "https://moikrug.ru/vacancies?q=%s&page=%d&sort=date";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));

    @Override
    String buildPageLink(int page) {
        return format(URL_TEMPLATE, searchWord, page);
    }

    @Override
    Elements getAllVacancyRowsOnPage(Document doc) {
        return doc.getElementsByClass("job");
    }

    @Override
    String grabTitle(Element row) {
        String title = getInnerClassText(row, "title");
        // e.g. many java-vacancies do not contain 'java' in their title on moikrug.ru
        return title.toLowerCase().contains(searchWord) ? title : setSkillIfMatched(row, title);
    }

    @Override
    String grabCity(Element row) {
        return getInnerClassText(row, "location");
    }

    @Override
    String grabCompany(Element row) {
        return getInnerClassText(row, "company_name");
    }

    @Override
    String grabDateTime(Element row) {
        return getInnerClassText(row, "date");
    }

    @Override
    ZonedDateTime parseDateTime(String dateLine) {
        return ZonedDateTime.of(
                LocalDate.parse(dateLine, FORMATTER), LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
                ZoneId.systemDefault());
    }

    @Override
    String grabLink(Element row) {
        return SITE_NAME + row.getElementsByClass("job_icon").first().attr("href");
    }

    @Override
    String grabDescription(Element row) {
        return row.getElementsByClass("skill")
                .stream()
                .map(Element::text)
                .collect(Collectors.joining("\n"));
    }

    private String setSkillIfMatched(Element row, String title) {
        boolean noneMatched = row.getElementsByClass("skill")
                .stream()
                .map(Element::text)
                .noneMatch(searchWord::equalsIgnoreCase);
        return noneMatched ? title : '[' + searchWord + "] " + title;
    }

    private String getInnerClassText(Element e, String className) {
        Element first = e.getElementsByClass(className).first();
        return first != null ? first.text() : "";
    }
}