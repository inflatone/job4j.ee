package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import ru.job4j.jobseeker.service.executor.ExecutionService;
import ru.job4j.jobseeker.service.executor.ParserBatch;
import ru.job4j.jobseeker.service.executor.TaskLauncher;
import ru.job4j.jobseeker.service.executor.WebJobExecutor;
import ru.job4j.vacancy.JobExecutor;
import ru.job4j.vacancy.jsoup.HabrCareerJsoupProcessor;
import ru.job4j.vacancy.jsoup.HhRuJsoupProcessor;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.SqlRuJsoupProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
public class ExecutionModule extends AbstractModule {
    private final String quartzProperties;

    public ExecutionModule(String quartzProperties) {
        this.quartzProperties = quartzProperties;
    }

    @Override
    protected void configure() {
        //parsers
        Multibinder<JsoupProcessor> processorBinder = Multibinder.newSetBinder(binder(), JsoupProcessor.class);
        processorBinder.addBinding().to(HhRuJsoupProcessor.class).asEagerSingleton();
        processorBinder.addBinding().to(HabrCareerJsoupProcessor.class).asEagerSingleton();
        processorBinder.addBinding().to(SqlRuJsoupProcessor.class).asEagerSingleton();

        bind(JsoupProcessor.class).to(ParserBatch.class).asEagerSingleton();
        bind(TaskLauncher.class).to(ExecutionService.class).asEagerSingleton();

        bind(JobFactory.class).to(GuiceJobFactory.class).asEagerSingleton();
        bind(JobExecutor.class).to(WebJobExecutor.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public WebJobExecutor provideJobExecutor(JobFactory jobFactory) throws SchedulerException {
        return new WebJobExecutor(quartzProperties, jobFactory);
    }

    // http://blog.timmattison.com/archives/2014/08/05/using-guice-dependency-injection-with-quartz-schedulding/
    private static class GuiceJobFactory implements JobFactory {
        private final Injector injector;

        @Inject
        public GuiceJobFactory(Injector injector) {
            this.injector = injector;
        }

        @Override
        public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
            var jobDetail = bundle.getJobDetail();
            var jobClass = jobDetail.getJobClass();
            log.info("Create new {} instance to launch {}", jobClass, jobDetail.getKey());
            return injector.getInstance(jobClass);
        }
    }
}
