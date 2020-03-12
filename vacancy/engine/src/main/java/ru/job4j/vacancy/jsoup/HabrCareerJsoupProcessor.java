package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.vacancy.model.SourceTitle;

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
    private static final String SITE_NAME = "https://career.habr.com";
    private static final String URL_TEMPLATE = "https://career.habr.com/vacancies?q=%s&page=%d&sort=date";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));

    @Override
    public SourceTitle getSourceTitle() {
        return SourceTitle.habr_com;
    }

    @Override
    String buildPageLink(int page, ParseParameters params) {
        return format(URL_TEMPLATE, params.getSearchQuery(), page);
    }

    @Override
    Elements getAllVacancyRowsOnPage(Document doc) {
        return doc.getElementsByClass("job");
    }

    @Override
    String grabTitleIfValid(Element row, ParseParameters params) {
        var title = getInnerClassText(row, "title");
        // e.g. many java-vacancies do not contain 'java' in their title on habr.com
        var keyword = params.getKeyword();
        var result = title.toLowerCase().contains(keyword) ? title : setSkillIfMatched(row, title, keyword);
        return params.isValidTitle(result) ? result : null;
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

    private String setSkillIfMatched(Element row, String title, String keyword) {
        boolean noneMatched = row.getElementsByClass("skill")
                .stream()
                .map(Element::text)
                .noneMatch(keyword::equalsIgnoreCase);
        return noneMatched ? title : '[' + keyword + "] " + title;
    }

    private String getInnerClassText(Element e, String className) {
        Element first = e.getElementsByClass(className).first();
        return first != null ? first.text() : "";
    }
}