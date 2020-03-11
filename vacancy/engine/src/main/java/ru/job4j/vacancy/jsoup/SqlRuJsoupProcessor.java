package ru.job4j.vacancy.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.job4j.vacancy.util.JsoupHelper.buildDocument;

/**
 * Represents program strategy of sql.ru parsing
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
public class SqlRuJsoupProcessor extends AbstractJsoupProcessor {
    private static final String URL = "https://www.sql.ru/forum/job-offers/";

    @Override
    public void submitSearchWord(String word) {
        // unsupported on sql.ru
    }

    @Override
    String buildPageLink(int page) {
        return URL + page;
    }

    @Override
    Elements getAllVacancyRowsOnPage(Document doc) {
        return doc.getElementsByClass("forumTable").first()
                .getElementsByTag("tr")
                .stream()
                .filter(this::isValidRow)
                .collect(Collectors.toCollection(Elements::new));
    }

    private boolean isValidRow(Element row) {
        Elements topic = row.getElementsByClass("postslisttopic");
        return !topic.isEmpty() && !topic.html().startsWith("Важно:");
    }

    @Override
    String grabTitle(Element row) {
        return row.getElementsByClass("postslisttopic").first()
                .getElementsByTag("a").first()
                .text();
    }

    @Override
    String grabCity(Element row) {
        return null; // unsupported on sql.ru
    }

    @Override
    String grabCompany(Element row) {
        return null; // unsupported on sql.ru
    }

    @Override
    String grabLink(Element row) {
        return row.getElementsByClass("postslisttopic").first()
                .getElementsByTag("a").first()
                .attr("href");
    }

    @Override
    String grabDateTime(Element row) {
        Element dateColumn = row.getElementsByClass("altCol").last();
        return dateColumn.text();
    }

    @Override
    LocalDateTime parseDateTime(String dateTimeLine) {
        String[] dateTime = dateTimeLine.split(", ");
        return LocalDateTime.of(
                parseDate(dateTime[0]), LocalTime.parse(dateTime[1]));
    }

    private LocalDate parseDate(String dateLine) {
        LocalDate date;
        if ("сегодня".equals(dateLine)) {
            date = LocalDate.now();
        } else if ("вчера".equals(dateLine)) {
            date = LocalDate.now().minusDays(1);
        } else {
            date = LocalDate.parse(dateLine, FormatterHolder.FORMATTER);
        }
        return date;
    }

    @Override
    String grabDescription(Element row) throws IOException {
        String link = grabLink(row);
        Document document = buildDocument(link);
        Elements elements = document.getElementsByClass("msgTable");
        Element first = elements.first();
        Elements msgBody = first.getElementsByClass("msgBody");
        String html = msgBody.get(1).html();
        return cleanHtml(html);
    }

    /**
     * Cleans submitted html sequence from all tags, also replaces <br> tags on line separator symbol
     *
     * @param html row html
     * @return text from html
     */
    private String cleanHtml(String html) {
        // https://www.codermag.net/2016/02/remove-html-from-string-using-jsoup.html
        Whitelist whitelist = new Whitelist().addTags("br");
        String text = Jsoup.clean(html, whitelist);
        text = text.replace("<br>", "");
        return text.replaceAll("\\n\\s+", "\n");
    }

    /**
     * Customized date-time formatter holder with the month abbreviations which accepted on sql.ru forum
     */
    private static final class FormatterHolder {
        private static final DateTimeFormatter FORMATTER;

        static {
            // https://stackoverflow.com/questions/30286612/how-to-change-dateformatsymbols-in-jdk8-to-solve-incompatibility-with-jdk7-geni
            Map<Long, String> lookup = new HashMap<>();
            lookup.put(1L, "янв");
            lookup.put(2L, "фев");
            lookup.put(3L, "мар");
            lookup.put(4L, "апр");
            lookup.put(5L, "май");
            lookup.put(6L, "июн");
            lookup.put(7L, "июл");
            lookup.put(8L, "авг");
            lookup.put(9L, "сен");
            lookup.put(10L, "окт");
            lookup.put(11L, "ноя");
            lookup.put(12L, "дек");

            FORMATTER = new DateTimeFormatterBuilder()
                    .appendPattern("d ")
                    .appendText(ChronoField.MONTH_OF_YEAR, lookup)
                    .appendPattern(" yy")
                    .toFormatter();
        }
    }
}