package ru.job4j.vacancy.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import ru.job4j.vacancy.model.VacancyData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import static org.mockito.Mockito.doAnswer;
import static ru.job4j.vacancy.TestUtil.of;

class HabrCareerJsoupProcessorTest extends AbstractJsoupProcessorTest {
    private static final VacancyData AUGUST_VACANCY = new VacancyData(
            "Требуется программист (Москва, Сбербанк)", "https://career.habr.com/mock.url", "test description\ntest details",
            of(2019, Month.AUGUST, 12));

    private static final VacancyData JUNE_VACANCY = new VacancyData(
            "Требуется уборщица (Москва, Сбербанк)", "https://career.habr.com/mock.url", "test description\ntest details",
            of(2019, Month.JUNE, 20));

    private static final Document EMPTY_MOCK_PAGE_HABR_CAREER = Jsoup.parse("<div class=\"jobs show_marked\" id=\"jobs_list\"></div>");

    private static final Document MOCK_PAGE_HABR_CAREER = Jsoup.parse("<div class=\"jobs show_marked\" id=\"jobs_list\">"
            + "    <div class=\"job  \" id=\"job_1000012345\">"
            + "        <a class=\"job_icon\" href=\"/mock.url\"></a>"
            + "        <span class=\"date\">12 августа 2019</span>"
            + "        <div class=\"info\">"
            + "            <div class=\"title\" title=\"Middle Java developer\"><a href=\"/mock.url\">Требуется программист</a></div>"
            + "            <div class=\"skills\">"
            + "                <a class=\"skill\" href=\"\">test description</a><a class=\"skill\" href=\"\">test details</a>"
            + "            </div>"
            + "            <div class=\"meta v2\">"
            + "                <span class=\"company_name\"><a href=\"\">Сбербанк</a></span>"
            + "                <span class=\"location\"><a href=\"\">Москва</a></span>"
            + "            </div>"
            + "        </div>"
            + "    </div>"
            + "    <div class=\"job  \" id=\"job_1000012346\">"
            + "        <a class=\"job_icon\" href=\"/mock.url\"></a>"
            + "        <span class=\"date\">20 июня 2019</span>"
            + "        <div class=\"info\">"
            + "            <div class=\"title\" title=\"Middle Java developer\"><a href=\"/mock.url\">Требуется уборщица</a></div>"
            + "            <div class=\"skills\">"
            + "                <a class=\"skill\" href=\"\">test description</a><a class=\"skill\" href=\"\">test details</a>"
            + "            </div>"
            + "            <div class=\"meta v2\">"
            + "                <span class=\"company_name\"><a href=\"\">Сбербанк</a></span>"
            + "                <span class=\"location\"><a href=\"\">Москва</a></span>"
            + "            </div>"
            + "        </div>"
            + "    </div>"
            + "</div>");

    HabrCareerJsoupProcessorTest() {
        super(new HabrCareerJsoupProcessor(), AUGUST_VACANCY, JUNE_VACANCY);
    }

    @Override
    Element mockRow() {
        return MOCK_PAGE_HABR_CAREER.getElementsByClass("job").first();
    }

    @Override
    void mockDocument(ParseParameters params) throws IOException {
        doAnswer(invocation -> MOCK_PAGE_HABR_CAREER)
                .when(processor)
                .buildDocument(String.format("https://career.habr.com/vacancies?q=%s&page=1&sort=date",
                        params.getKeyword() + (params.getCity() == null ? "" : '+' + params.getCity())));
    }

    @Override
    void mockEmptyDocument() throws IOException {
        doAnswer(invocation -> EMPTY_MOCK_PAGE_HABR_CAREER)
                .when(processor)
                .buildDocument("https://career.habr.com/vacancies?q=&page=1&sort=date");
    }

    // unit tests

    @Test
    void getAllVacancyRowsOnPage() {
        Document rows = Jsoup.parse(
                "<div class=\"jobs show_marked\" id=\"jobs_list\">"
                        + "    <div class=\"job  \" id=\"job_1000052834\"></div>"
                        + "    <div class=\"job  \" id=\"job_1000044214\"></div>"
                        + "</div>");
        tester.getAllVacancyRowsOnPage(2, rows);
    }

    @Test
    void mainLoopCountCircles() throws IOException {
        tester.mainLoopCountCircles(1000, i -> i < 1000); // nothing could stop the main loop iterations
    }

    @Test
    void buildPageLink() {
        var expected = "https://career.habr.com/vacancies?q=test&page=10&sort=date";
        tester.buildPageLink(expected, "test", 10);
    }

    @Test
    void composeTitle() {
        Document row = Jsoup.parse("<div class=\"info\">"
                + "            <div class=\"title\" title=\"Middle Java developer\">"
                + "                <a href=\"/mock.url\">Java Developer</a>"
                + "            </div>"
                + "            <div class=\"meta v2\">"
                + "                <span class=\"company_name\"><a href=\"\">Company</a></span>"
                + "                <span class=\"location\"><a href=\"\">City</a></span>"
                + "            </div>"
                + "        </div>");
        tester.composeTitle("Java Developer (City, Company)", row);
    }

    @Test
    void composeTitleTestSkills() {
        Document row = Jsoup.parse("<div class=\"info\">"
                + "            <div class=\"title\" title=\"Middle Java\">"
                + "                <a href=\"/mock.url\">Java Programmer</a>"
                + "            </div>"
                + "            <div class=\"skills\">"
                + "                <a class=\"skill\" href=\"\">SQL</a>"
                + "                <a class=\"skill\" href=\"\">REST</a>"
                + "            </div>"
                + "            <div class=\"meta v2\">"
                + "                <span class=\"company_name\"><a href=\"\">Company</a></span>"
                + "                <span class=\"location\"><a href=\"\">City</a></span>"
                + "            </div>"
                + "        </div>");
        tester.composeTitle("Java Programmer (City, Company)", row);
    }

    @Test
    void composeTitleWithoutCity() {
        Document row = Jsoup.parse("<div class=\"info\">"
                + "            <div class=\"title\" title=\"Middle Java developer\">"
                + "                <a href=\"/mock.url\">Java Developer</a>"
                + "            </div>"
                + "            <div class=\"meta v2\">"
                + "                <span class=\"company_name\"><a href=\"\">Company</a></span>"
                + "            </div>"
                + "        </div>");
        tester.composeTitle("Java Developer (Company)", row);
    }

    @Test
    void composeTitleWithoutCityAndCompany() {
        Document row = Jsoup.parse("<div class=\"info\">"
                + "            <div class=\"title\" title=\"Middle Java\">"
                + "                <a href=\"/mock.url\">Programmer</a>"
                + "            </div>"
                + "            <div class=\"skills\">"
                + "                <a class=\"skill\" href=\"\">SQL</a>"
                + "                <a class=\"skill\" href=\"\">Java</a>"
                + "                <a class=\"skill\" href=\"\">REST</a>"
                + "            </div>"
                + "            <div class=\"meta v2\">"
                + "            </div>"
                + "        </div>");
        tester.composeTitle("[java] Programmer", row);
    }

    @Test
    void grabDateTime() {
        var row = Jsoup.parse("<div class=\"inner\">"
                + "    <a class=\"job_icon\" href=\"/mock.url\"></a>"
                + "    <span class=\"date\">test_date</span>"
                + "    <div class=\"info\"></div>"
                + "</div>");
        tester.grabDateTime(row);
    }

    @Test
    void parseDateTime() {
        tester.parseDate(LocalDate.of(2015, 8, 3),
                "3 августа 2015");
    }

    @Test
    void grabLink() {
        var row = Jsoup.parse("<div class=\"inner\">"
                + "    <a class=\"job_icon\" href=\"//mock.url\"></a>"
                + "    <span class=\"date\">test_date</span>"
                + "    <div class=\"info\">"
                + "        <div class=\"title\" title=\"Developer\"><a href=\"/wrong./mock.url\">Java developer</a></div>"
                + "    </div>"
                + "</div>");
        tester.grabLink("https://career.habr.com//mock.url", row);
    }

    @Test
    void grabDescription() throws IOException {
        Document row = Jsoup.parse("<div class=\"info\">"
                + "            <div class=\"title\" title=\"Middle Java\">"
                + "                <a href=\"/mock.url\">Programmer</a>"
                + "            </div>"
                + "            <div class=\"skills\">"
                + "                <a class=\"skill\" href=\"\">SQL</a>"
                + "                <a class=\"skill\" href=\"\">Java</a>"
                + "                <a class=\"skill\" href=\"\">REST</a>"
                + "            </div>"
                + "            <div class=\"meta v2\">"
                + "            </div>"
                + "        </div>");
        tester.grabDescription("SQL\nJava\nREST", row); // skills splitted by \n
    }
}