package ru.job4j.vacancy.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import ru.job4j.vacancy.model.VacancyData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.LocalDate.now;
import static org.mockito.BDDMockito.given;
import static ru.job4j.vacancy.util.JsoupHelper.buildDocument;

public class HhRuJsoupProcessorTest extends AbstractJsoupProcessorTest {
    private static final VacancyData EXPECTED_VACANCY_HH_RU = new VacancyData(
            "Требуется программист (Москва, Сбербанк)", "hh.ru/topic.mock.url", "test description\ntest details",
            LocalDateTime.of(2019, 8, 12, 0, 0));

    private static final Document EMPTY_MOCK_PAGE_HH_RU = Jsoup.parse(
            "<div><div class=\"vacancy-serp\"></div></div>");

    private static final Document MOCK_PAGE_HH_RU = Jsoup.parse("<div class=\"vacancies\">"
            + "    <div data-qa=\"vacancy-serp__vacancy\">"
            + "        <div class=\"resume-search-item__name\">"
            + "            <a data-qa=\"vacancy-serp__vacancy-title\" href=\"hh.ru/topic.mock.url\">Требуется программист</a></div>"
            + "        <div class=\"vacancy-serp-item__meta-info\"><a data-qa=\"vacancy-serp__vacancy-employer\">Сбербанк</a></div>"
            + "        <div class=\"vacancy-serp-item__meta-info\"><span data-qa=\"vacancy-serp__vacancy-address\">Москва</span></div>"
            + "        <div data-qa=\"vacancy-serp__vacancy_snippet_responsibility\">test description</div>"
            + "        <div data-qa=\"vacancy-serp__vacancy_snippet_requirement\">test details</div>"
            + "        <span data-qa=\"vacancy-serp__vacancy-date\">12 августа</span>"
            + "    </div>"
            + "    <div data-qa=\"vacancy-serp__vacancy\">"
            + "        <div class=\"resume-search-item__name\">"
            + "            <a data-qa=\"vacancy-serp__vacancy-title\" href=\"hh.ru/topic.mock.url\">Требуется уборщица</a></div>"
            + "        <div class=\"vacancy-serp-item__meta-info\"><a data-qa=\"vacancy-serp__vacancy-employer\">Сбербанк</a></div>"
            + "        <div class=\"vacancy-serp-item__meta-info\"><span data-qa=\"vacancy-serp__vacancy-address\">Москва</span></div>"
            + "        <div data-qa=\"vacancy-serp__vacancy_snippet_responsibility\">test description</div>"
            + "        <div data-qa=\"vacancy-serp__vacancy_snippet_requirement\">test details</div>"
            + "        <span data-qa=\"vacancy-serp__vacancy-date\">20 июля</span>"
            + "    </div>"
            + "</div>");

    public HhRuJsoupProcessorTest() {
        super(new HhRuJsoupProcessor(), EXPECTED_VACANCY_HH_RU);
    }

    // template methods for integration tests in superclass

    @Override
    Element mockRow() {
        return MOCK_PAGE_HH_RU.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy").first();
    }

    @Override
    void mockDocument() throws IOException {
        given(buildDocument("http://hh.ru/search/vacancy?text=&page=0&items_on_page=100&order_by=publication_time"))
                .willReturn(MOCK_PAGE_HH_RU);
    }

    @Override
    void mockEmptyDocument() throws IOException {
        given(buildDocument("http://hh.ru/search/vacancy?text=&page=0&items_on_page=100&order_by=publication_time"))
                .willReturn(EMPTY_MOCK_PAGE_HH_RU);
    }

    // unit tests

    @Test
    public void getAllVacancyRowsOnPage() {
        Document rows = Jsoup.parse(
                "<div class=\"vacancy-serp\">"
                        + "    <div data-qa=\"vacancy-serp__vacancy vacancy-serp__vacancy_premium\"></div>"
                        + "    <div data-qa=\"vacancy-serp__vacancy\" class=\"vacancy-serp-item \"></div>"
                        + "    <div data-qa=\"vacancy-serp__vacancy\" class=\"vacancy-serp-item \"></div>"
                        + "</div>");
        tester.getAllVacancyRowsOnPage(3, rows);
    }

    @Test
    public void mainLoopCountCircles() throws IOException {
        tester.mainLoopCountCircles(20, i -> i < 1000); // after 20 pages the forced break has to happen on hh.ru strategy
    }

    @Test
    public void buildPageLink() {
        var expected = "http://hh.ru/search/vacancy?text=test&page=9&items_on_page=100&order_by=publication_time";
        tester.buildPageLink(expected, "test", 10);
    }

    @Test
    public void buildPageLinkNullSearchWord() {
        var expected = "http://hh.ru/search/vacancy?text=&page=4&items_on_page=100&order_by=publication_time";
        tester.buildPageLink(expected, null, 5);
    }

    @Test
    public void composeTitle() {
        Document row = Jsoup.parse("<div data-qa=\"vacancy-serp__vacancy-title\">Java Developer</div>"
                + "<span data-qa=\"vacancy-serp__vacancy-address\">City</span>"
                + "<a data-qa=\"vacancy-serp__vacancy-employer\">Company</a>");
        tester.composeTitle("Java Developer (City, Company)", row);
    }

    @Test
    public void composeTitleWithoutCity() {
        Document row = Jsoup.parse("<div data-qa=\"vacancy-serp__vacancy-title\">Java Developer</div>"
                + "<a data-qa=\"vacancy-serp__vacancy-employer\">Company</a>");
        tester.composeTitle("Java Developer (Company)", row);
    }

    @Test
    public void composeTitleWithoutCityAndCompany() {
        Document row = Jsoup.parse("<div data-qa=\"vacancy-serp__vacancy-title\">Java Developer</div>");
        tester.composeTitle("Java Developer", row);
    }

    @Test
    public void grabDateTime() {
        var row = Jsoup.parse("<div class=\"vacancy-serp-item__row vacancy-serp-item__row_controls\">"
                + "        <span data-qa=\"vacancy-serp__vacancy-date\">"
                + "            <span class=\"vacancy-serp-item__publication-date\">test_date</span>"
                + "        </span>"
                + "    </div>");
        tester.grabDateTime(row);
    }

    @Test
    public void parseDateTime() {
        tester.parseDate(dateOf(8, 3),
                "3 августа");
    }

    @Test
    public void parseDateTimeEmpty() {
        tester.parseDate(now(), "");
    }

    @Test
    public void parseDateTimeEndOfYear() {
        LocalDate endOfYear = dateOf(12, 31);
        if (!LocalDate.now().isEqual(endOfYear)) { // test won't be succeeded on 31 DEC — should skip it
            tester.parseDate(endOfYear.minusYears(1), // should be parsed as last year date
                    "31 декабря");
        }
    }

    @Test
    public void grabLink() {
        var row = Jsoup.parse("<div class=\"resume-search-item__name\"><a"
                + "            data-qa=\"vacancy-serp__vacancy-title\""
                + "            href=\"http://mock.url\">Java Developer</a>"
                + "    </div>");
        tester.grabLink("http://mock.url", row);
    }

    @Test
    public void grabDescription() throws IOException {
        var row = Jsoup.parse("<div class=\"vacancy-serp-item__row\">"
                + "        <div data-qa=\"vacancy-serp__vacancy_snippet_responsibility\">"
                + "            test description"
                + "        </div>"
                + "        <div data-qa=\"vacancy-serp__vacancy_snippet_requirement\">"
                + "            test details"
                + "        </div>"
                + "    </div>");
        tester.grabDescription("test description\ntest details", row); // responsibility + \n + requirement
    }

    @Test
    public void anyMorePages() {
        tester.anyMorePages(true, 10);
        tester.anyMorePages(false, 30);
    }

    private LocalDate dateOf(int month, int dayOfMonth) {
        return LocalDate.of(now().getYear(), month, dayOfMonth);
    }
}