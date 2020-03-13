package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import ru.job4j.vacancy.TestUtil;
import ru.job4j.vacancy.model.VacancyData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static java.time.Month.JULY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static ru.job4j.vacancy.TestUtil.JAVA_DEFAULT_PARAMS;


abstract class AbstractJsoupProcessorTest {
    private static final ZonedDateTime LIMIT_DATE = TestUtil.of(2019, JULY, 31, 16, 0);

    private static final ParseParameters PARAMS = ParseParameters.of("", LIMIT_DATE);

    private final VacancyData augustVacancy;
    private final VacancyData juneVacancy;

    final AbstractJsoupProcessor processor;
    final JsoupProcessorTester tester;

    AbstractJsoupProcessorTest(AbstractJsoupProcessor processor, VacancyData augustVacancy, VacancyData juneVacancy) {
        this.processor = spy(processor);
        this.tester = new JsoupProcessorTester(this.processor);
        this.augustVacancy = augustVacancy;
        this.juneVacancy = juneVacancy;
    }

    abstract Element mockRow();

    abstract void mockDocument(ParseParameters params) throws IOException;

    abstract void mockEmptyDocument() throws IOException;

    @Test
    void processPage() throws IOException {
        mockDocument(PARAMS);
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processPage(buffer, 1, PARAMS);
        assertFalse(continued);
        assertEquals(1, buffer.size());
        assertEquals(augustVacancy, buffer.get(0));
    }

    @Test
    void processPageWithKeywordAndCity() throws IOException {
        var params = ParseParameters.of("программист", "Москва", LIMIT_DATE);
        mockDocument(params);
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processPage(buffer, 1, params);
        assertFalse(continued);
        assertEquals(1, buffer.size());
        assertEquals(augustVacancy, buffer.get(0));
    }

    @Test
    void processPageWithFinishLimit() throws IOException {
        var params = ParseParameters.of("", null, LIMIT_DATE.minusMonths(2), LIMIT_DATE.minusMonths(1));
        mockDocument(params);
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processPage(buffer, 1, params);
        assertTrue(continued);
        assertEquals(1, buffer.size());
        assertEquals(juneVacancy, buffer.get(0));
    }

    @Test
    void processEmptyPage() throws IOException {
        mockEmptyDocument();
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processPage(buffer, 1, PARAMS);
        assertFalse(continued);
        assertTrue(buffer.isEmpty());
    }

    @Test
    void processRow() throws IOException {
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processRow(buffer, mockRow(), PARAMS);
        assertTrue(continued);
        assertEquals(1, buffer.size());
        assertEquals(augustVacancy, buffer.get(0));
    }

    @Test
    void processRowDateLimited() throws IOException {
        ArrayList<VacancyData> buffer = new ArrayList<>();
        var params = ParseParameters.of("", LocalDateTime.MAX.atZone(ZoneId.systemDefault()));
        boolean continued = processor.processRow(buffer, mockRow(), params);
        assertFalse(continued);
        assertTrue(buffer.isEmpty());
    }

    @Test
    void grabRow() throws IOException {
        Optional<VacancyData> vacContainer = processor.grabRow(mockRow(), augustVacancy.getDateTime(), PARAMS); // method's parameter = already parsed date
        assertTrue(vacContainer.isPresent());
        VacancyData vacancy = vacContainer.get();
        assertEquals(augustVacancy, vacancy);
    }

    @Test
    void grabRowNotPassed() throws IOException {
        Optional<VacancyData> vacContainer = processor.grabRow(mockRow(), augustVacancy.getDateTime(), JAVA_DEFAULT_PARAMS); // method's parameter = already parsed date
        assertTrue(vacContainer.isEmpty());
    }
}