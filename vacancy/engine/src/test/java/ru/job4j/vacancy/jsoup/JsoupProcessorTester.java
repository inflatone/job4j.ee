package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mockito.invocation.InvocationOnMock;
import ru.job4j.vacancy.model.VacancyData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JsoupProcessorTester {
    private final AbstractJsoupProcessor processor;

    JsoupProcessorTester(AbstractJsoupProcessor processor) {
        this.processor = processor;
    }

    void submitSearchWord(String searhword) {
        processor.submitSearchWord(searhword);
    }

    void getAllVacancyRowsOnPage(int expectedRowCount, Document doc) {
        Elements rowsOnPage = processor.getAllVacancyRowsOnPage(doc);
        assertEquals(expectedRowCount, rowsOnPage.size());
    }

    void mainLoopCountCircles(int expectedCount, Predicate<Integer> iterationEscape) throws IOException {
        List<VacancyData> vacancies = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
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
        mockProcessor.mainLoop(vacancies, now);
        assertEquals(expectedCount, counter[0]);
    }

    void buildPageLink(String expected, String searchWord, int pageNum) {
        processor.submitSearchWord(searchWord);
        var url = processor.buildPageLink(pageNum);
        assertEquals(expected, url);
    }

    void composeTitle(String expected, Element row) {
        var title = processor.composeTitle(row);
        assertEquals(expected, title);
    }

    void grabDateTime(Element row) {
        var date = processor.grabDateTime(row);
        assertEquals("test_date", date);
    }

    void parseDate(LocalDate expected, String dateLine) {
        parseDateTime(expected, LocalTime.MIN, dateLine);
    }

    void parseDateTime(LocalDate expectedDate, LocalTime expectedTime, String dateTimeLine) {
        var dateTime = processor.parseDateTime(dateTimeLine);
        assertEquals(LocalDateTime.of(expectedDate, expectedTime), dateTime);
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