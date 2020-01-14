package ru.job4j.vacancy;

import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.model.VacancyData;

import java.util.List;

import static ru.job4j.vacancy.TestUtil.JAVA_DEFAULT_PARAMS;
import static ru.job4j.vacancy.TestUtil.asFullPathString;

/**
 * For manual testing
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-15
 */
class ManualTest {
    public static void main(String[] args) throws Exception {
        //System.out.println(getVacancies(new SqlRuJsoupProcessor()).size());
//        System.out.println(getVacancies(new HhRuJsoupProcessor()).size());
//        System.out.println(getVacancies(new HabrCareerJsoupProcessor()).size());

        VacancyCollectorApp.main(new String[]{asFullPathString("job.properties")});
    }

    private static List<VacancyData> getVacancies(JsoupProcessor processor) {
        return processor.parseVacancies(JAVA_DEFAULT_PARAMS);
    }
}
