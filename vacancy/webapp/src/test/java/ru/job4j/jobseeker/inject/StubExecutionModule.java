package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import ru.job4j.jobseeker.service.executor.ExecutionService;
import ru.job4j.jobseeker.service.executor.ParserBatch;
import ru.job4j.jobseeker.service.executor.WebJobExecutor;
import ru.job4j.vacancy.jsoup.AbstractJsoupProcessor;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.model.SourceTitle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StubExecutionModule extends AbstractModule {
    @Override
    protected void configure() {
        //parsers
        Multibinder<JsoupProcessor> processorBinder = Multibinder.newSetBinder(binder(), JsoupProcessor.class);
        JsoupProcessor mockProcessor = mock(AbstractJsoupProcessor.class);
        when(mockProcessor.getSourceTitle()).thenReturn(SourceTitle.hh_ru);

        processorBinder.addBinding().toInstance(mockProcessor);

        bind(ExecutionService.class).asEagerSingleton();
        bind(JsoupProcessor.class).to(ParserBatch.class).asEagerSingleton();
        bind(WebJobExecutor.class).toInstance(mock(WebJobExecutor.class));
    }
}
