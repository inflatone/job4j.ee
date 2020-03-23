package ru.job4j.jobseeker.service.executor;

import com.google.common.collect.Maps;
import one.util.streamex.StreamEx;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.ParseParameters;
import ru.job4j.vacancy.model.SourceTitle;
import ru.job4j.vacancy.model.VacancyData;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All the parsers storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-28
 */
public class ParserBatch implements JsoupProcessor {
    private final Map<SourceTitle, JsoupProcessor> processors;

    @Inject
    public ParserBatch(Set<JsoupProcessor> processors) {
        // https://stackoverflow.com/q/20363719/10375242#comment45946660_20363874
        this.processors = Maps.uniqueIndex(processors, JsoupProcessor::getSourceTitle);
    }

    @Override
    public List<VacancyData> parseVacancies(ParseParameters params) {
        return StreamEx.of(params.getSources())
                .parallel()
                .map(processors::get)
                .flatMap(p -> p.parseVacancies(params).stream())
                .toList();
    }
}
