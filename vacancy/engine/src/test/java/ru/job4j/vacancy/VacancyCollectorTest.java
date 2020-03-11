package ru.job4j.vacancy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.ObjectAlreadyExistsException;
import org.slf4j.Logger;
import ru.job4j.vacancy.TestUtil.JobExample;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static ru.job4j.vacancy.TestUtil.asFullPathString;

public class VacancyCollectorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testStart() throws Exception {
        String jobProperties = asFullPathString("job.properties");
        String[] args = {jobProperties};
        VacancyCollector collector = new VacancyCollector(args);
        collector.start(JobExample.class);
        assertTrue(collector.shutdown(false));
    }

    @Test
    public void testStartWithoutProperties() throws Exception {
        VacancyCollector collector = new VacancyCollector(new String[]{});
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("need single *.properties file path at least");
        collector.start(JobExample.class);
        collector.shutdown(false);
    }

    @Test
    public void testShutdownBeforeStart() throws Exception {
        VacancyCollector collector = new VacancyCollector(new String[]{});
        assertFalse(collector.shutdown(false));
    }

    @Test
    public void testStartDuplicateTask() throws Exception {
        String jobProperties = asFullPathString("job.properties");
        String[] args = {jobProperties};
        VacancyCollector collector = new VacancyCollector(args);
        collector.start(JobExample.class);
        try {
            collector.start(JobExample.class);
            fail("not supposed to pass here");
        } catch (ObjectAlreadyExistsException e) {
            assertTrue(e.getMessage().contains("already exists with this identification"));
            collector.shutdown(false);
        }
    }

    @Test
    public void testDebugTurnedOn() {
        String[] args = {"-debug"};
        VacancyCollector collector = new VacancyCollector(args);
        Logger mockLogger = mock(Logger.class);

        doAnswer(i -> {
            String message = i.getArgument(0);
            assertEquals("Error: test exception", message);
            return null;
        }).when(mockLogger).error(anyString(), any(Throwable.class));

        doAnswer(i -> {
            fail("not expected");
            return null;
        }).when(mockLogger).error(anyString());

        collector.handleException(mockLogger, new IllegalArgumentException("test exception"));
    }

    @Test
    public void testDebugTurnedOff() {
        String[] args = {};
        VacancyCollector collector = new VacancyCollector(args);
        Logger mockLogger = mock(Logger.class);

        doAnswer(i -> {
            fail("not expected");
            return null;
        }).when(mockLogger).error(anyString(), any(Throwable.class));

        doAnswer(i -> {
            String message = i.getArgument(0);
            assertEquals("Error: test exception", message);
            return null;

        }).when(mockLogger).error(anyString());

        collector.handleException(mockLogger, new IllegalArgumentException("test exception"));
    }


}