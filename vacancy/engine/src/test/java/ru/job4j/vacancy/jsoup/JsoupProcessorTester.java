package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mockito.invocation.InvocationOnMock;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.util.TimeUtil;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.job4j.vacancy.TestUtil.JAVA_DEFAULT_PARAMS;

class JsoupProcessorTester {
    private final AbstractJsoupProcessor processor;

    JsoupProcessorTester(AbstractJsoupProcessor processor) {
        this.processor = processor;
    }

    void getAllVacancyRowsOnPage(int expectedRowCount, Document doc) {
        Elements rowsOnPage = processor.getAllVacancyRowsOnPage(doc);
        assertEquals(expectedRowCount, rowsOnPage.size());
    }

    void mainLoopCountCircles(int expectedCount, Predicate<Integer> iterationEscape) throws IOException {
        List<VacancyData> vacancies = new ArrayList<>();
        AbstractJsoupProcessor mockProcessor = mock(processor.getClass());
        final int[] counter = {0};
        when(mockProcessor.processPage(anyList(), anyInt(), any())).thenAnswer(invocation -> {
            int pageNum = invocation.getArgument(1);
            int i = ++counter[0];
            assertEquals(i, pageNum);
            return iterationEscape.test(i);
        });
        doAnswer(InvocationOnMock::callRealMethod)
                .when(mockProcessor).mainLoop(anyList(), any());
        doAnswer(InvocationOnMock::callRealMethod)
                .when(mockProcessor).anyMorePages(anyInt());
        mockProcessor.mainLoop(vacancies, ParseParameters.of("", null, LocalDateTime.now().atZone(ZoneId.systemDefault())));
        assertEquals(expectedCount, counter[0]);
    }

    void buildPageLink(String expected, String searchWord, int pageNum) {
        var url = processor.buildPageLink(pageNum, ParseParameters.of(searchWord, TimeUtil.now()));
        assertEquals(expected, url);
    }

    void composeTitle(String expected, Element row) {
        var title = processor.checkAndComposeTitle(row, JAVA_DEFAULT_PARAMS);
        assertEquals(expected, title);
    }

    void grabDateTime(Element row) {
        var date = processor.grabDateTime(row);
        assertEquals("test_date", date);
    }

    void parseDate(LocalDate expected, String dateLine) {
        parseDateTime(expected, LocalTime.now().truncatedTo(ChronoUnit.MINUTES), dateLine);
    }

    void parseDateTime(LocalDate expectedDate, LocalTime expectedTime, String dateTimeLine) {
        var dateTime = processor.parseDateTime(dateTimeLine);
        //dateTime.toLocalDateTime()
        assertEquals(ZonedDateTime.of(LocalDateTime.of(expectedDate, expectedTime), ZoneId.systemDefault()), dateTime);
    }

    void parseDateTime(ZonedDateTime expectedDateTime, String dateTimeLine) {
        var dateTime = processor.parseDateTime(dateTimeLine);
        assertEquals(expectedDateTime, dateTime);
    }

    void grabLink(String expected, Element row) {
        var url = processor.grabLink(row);
        assertEquals(expected, url);
    }

    void grabDescription(String expected, Element row) throws IOException {
        var description = processor.grabDescription(row);
        assertEquals(expected, description);
    }

    void anyMorePages(boolean expected, int pageNum) {
        assertEquals(expected, processor.anyMorePages(pageNum));
    }
}