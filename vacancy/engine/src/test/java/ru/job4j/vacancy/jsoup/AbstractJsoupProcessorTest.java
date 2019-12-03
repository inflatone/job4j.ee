package ru.job4j.vacancy.jsoup;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.util.JsoupHelper;
import ru.job4j.vacancy.util.JsoupHelper.Filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static ru.job4j.vacancy.TestUtil.LIMIT_DATE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JsoupHelper.class})
// Powermock problem with jdk9 modules https://github.com/powermock/powermock/issues/864#issuecomment-447001997
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*"})
public abstract class AbstractJsoupProcessorTest {
    private final VacancyData expectedVacancy;
    private final AbstractJsoupProcessor processor;
    final JsoupProcessorTester tester;

    AbstractJsoupProcessorTest(AbstractJsoupProcessor processor, VacancyData expectedVacancy) {
        this.processor = processor;
        this.tester = new JsoupProcessorTester(processor);
        this.expectedVacancy = expectedVacancy;
    }

    abstract Element mockRow();

    abstract void mockDocument() throws IOException;

    abstract void mockEmptyDocument() throws IOException;

    @Before
    public void setUp() throws IOException {
        mockStatic(JsoupHelper.class);
    }

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
        boolean continued = processor.processRow(buffer, mockRow(), LocalDateTime.MAX);
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