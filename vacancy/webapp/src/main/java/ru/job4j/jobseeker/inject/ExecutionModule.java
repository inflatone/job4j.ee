package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import ru.job4j.jobseeker.service.executor.ExecutionService;
import ru.job4j.jobseeker.service.executor.ParserBatch;
import ru.job4j.vacancy.JobExecutor;
import ru.job4j.vacancy.jsoup.HabrCareerJsoupProcessor;
import ru.job4j.vacancy.jsoup.HhRuJsoupProcessor;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.SqlRuJsoupProcessor;

public class ExecutionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExecutionService.class).asEagerSingleton();
        bind(JobExecutor.class).asEagerSingleton();

        //parsers
        Multibinder<JsoupProcessor> processorBinder = Multibinder.newSetBinder(binder(), JsoupProcessor.class);
        processorBinder.addBinding().to(HhRuJsoupProcessor.class).asEagerSingleton();
        processorBinder.addBinding().to(HabrCareerJsoupProcessor.class).asEagerSingleton();
        processorBinder.addBinding().to(SqlRuJsoupProcessor.class).asEagerSingleton();

        bind(JsoupProcessor.class).to(ParserBatch.class);
    }
}
