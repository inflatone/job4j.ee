package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.vacancy.model.SourceTitle;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
        return doc.getElementsByClass("vacancy-card");
    }

    @Override
    String grabTitleIfValid(Element row, ParseParameters params) {
        var title = getInnerClassText(row, "vacancy-card__title");
        // e.g. many java-vacancies do not contain 'java' in their title on habr.com
        var keyword = params.getKeyword();
        var result = title.toLowerCase().contains(keyword) ? title : setSkillIfMatched(row, title, keyword);
        return params.isValidTitle(result) ? result : null;
    }

    @Override
    String grabCity(Element row) {
        return getTextFromHrefElement(row, "/vacancies?city_id");
    }

    @Override
    String grabCompany(Element row) {
        return getTextFromHrefElement(row, "/companies");
    }

    @Override
    String grabDateTime(Element row) {
        return row.getElementsByClass("basic-date").attr("datetime");
    }

    @Override
    ZonedDateTime parseDateTime(String dateLine) {
        return ZonedDateTime.parse(dateLine, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    String grabLink(Element row) {
        return SITE_NAME + row.getElementsByClass("vacancy-card__icon-link").first().attr("href");
    }

    @Override
    String grabDescription(Element row) {
        return getElementsStarting(row, "/vacancies?skills")
                .stream()
                .map(Element::text)
                .collect(Collectors.joining("\n"));
    }

    private String setSkillIfMatched(Element row, String title, String keyword) {
        boolean noneMatched = getElementsStarting(row, "/vacancies?skills")
                .stream()
                .map(Element::text)
                .noneMatch(keyword::equalsIgnoreCase);
        return noneMatched ? title : '[' + keyword + "] " + title;
    }

    private String getInnerClassText(Element e, String className) {
        Element first = e.getElementsByClass(className).first();
        return first != null ? first.text() : "";
    }

    private String getTextFromHrefElement(Element row, String valueStart) {
        Element element = getElementsStarting(row, valueStart).first();
        return element == null ? null : element.text();
    }

    private Elements getElementsStarting(Element row, String value) {
        return row.getElementsByAttributeValueStarting("href", value);
    }
}