package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.util.JsoupHelper.Filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static ru.job4j.vacancy.TestUtil.LIMIT_DATE;

public abstract class AbstractJsoupProcessorTest {
    private final VacancyData expectedVacancy;
    final AbstractJsoupProcessor processor;
    final JsoupProcessorTester tester;

    AbstractJsoupProcessorTest(AbstractJsoupProcessor processor, VacancyData expectedVacancy) {
        this.processor = spy(processor);
        this.tester = new JsoupProcessorTester(this.processor);
        this.expectedVacancy = expectedVacancy;
    }

    abstract Element mockRow();

    abstract void mockDocument() throws IOException;

    abstract void mockEmptyDocument() throws IOException;

    @Test
    public void processPage() throws IOException {
        mockDocument();
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processPage(buffer, 1, LIMIT_DATE);
        assertFalse(continued);
        assertEquals(1, buffer.size());
        assertEquals(expectedVacancy, buffer.get(0));
    }

    @Test
    public void processEmptyPage() throws IOException {
        mockEmptyDocument();
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processPage(buffer, 1, LIMIT_DATE);
        assertFalse(continued);
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void processRow() throws IOException {
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processRow(buffer, mockRow(), LIMIT_DATE);
        assertTrue(continued);
        assertEquals(1, buffer.size());
        assertEquals(expectedVacancy, buffer.get(0));
    }

    @Test
    public void processRowDateLimited() throws IOException {
        ArrayList<VacancyData> buffer = new ArrayList<>();
        boolean continued = processor.processRow(buffer, mockRow(), LocalDateTime.MAX.atZone(ZoneId.systemDefault()));
        assertFalse(continued);
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void grabRow() throws IOException {
        processor.submitSearchFilter(null);
        Optional<VacancyData> vacContainer = processor.grabRow(mockRow(), expectedVacancy.getDateTime()); // method's parameter = already parsed date
        assertTrue(vacContainer.isPresent());
        VacancyData vacancy = vacContainer.get();
        assertEquals(expectedVacancy, vacancy);
    }

    @Test
    public void grabRowNotPassed() throws IOException {
        processor.submitSearchFilter(Filters::javaFilter);
        Optional<VacancyData> vacContainer = processor.grabRow(mockRow(), expectedVacancy.getDateTime()); // method's parameter = already parsed date
        assertTrue(vacContainer.isEmpty());
    }
}