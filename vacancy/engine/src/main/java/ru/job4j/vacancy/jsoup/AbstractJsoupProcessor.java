package ru.job4j.vacancy.jsoup;

import com.google.common.base.Strings;
import one.util.streamex.StreamEx;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.vacancy.model.VacancyData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.job4j.vacancy.util.JsoupHelper.Filters.DEFAULT_FILTER;

/**
 * Represents template to create real site parsers
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-14
 */
public abstract class AbstractJsoupProcessor implements JsoupProcessor {
    public static final String USER_AGENT = "Mozilla/5.0 (jsoup)";

    final Logger log = LoggerFactory.getLogger(getClass());

    protected String searchWord = "";

    private Predicate<String> titleFilter = DEFAULT_FILTER;

    abstract String buildPageLink(int page);

    abstract Elements getAllVacancyRowsOnPage(Document doc);

    abstract String grabTitle(Element row);

    abstract String grabCity(Element row);

    abstract String grabCompany(Element row);

    abstract String grabDateTime(Element row);

    abstract LocalDateTime parseDateTime(String dateLine);

    abstract String grabLink(Element row);

    abstract String grabDescription(Element row) throws IOException;

    /**
     * Checks if site has more search result pages
     *
     * @param i index of page that has been processed just now
     * @return true, if iteration can be continued
     */
    boolean anyMorePages(int i) {
        return true; // by default we can continue endlessly, errors cannot occurs (only non-topic pages - handled it below)
    }

    @Override
    public void submitSearchWord(String word) {
        this.searchWord = word == null ? "" : word.toLowerCase();
    }

    @Override
    public List<VacancyData> parseVacancies(LocalDateTime dateLimit) {
        List<VacancyData> vacancies = new ArrayList<>();
        try {
            mainLoop(vacancies, dateLimit);
        } catch (Exception e) {
            log.error("Error occurs due html page parsing", e);
        }
        log.info("Find and parse {} new vacancies after {}", vacancies.size(), dateLimit);
        return vacancies;
    }

    @Override
    public void submitSearchFilter(Predicate<String> filter) {
        this.titleFilter = filter != null ? filter : DEFAULT_FILTER;
    }

    /**
     * Represents main loop of vacancy parser that processes page after page of the job-offers sql.ru forum section
     *
     * @param buffer    vacancy buffer
     * @param dateLimit date limiter
     * @throws IOException if the input-output error occurs
     */
    void mainLoop(List<VacancyData> buffer, LocalDateTime dateLimit) throws IOException {
        int i = 0;
        do {
            i++;
        } while (processPage(buffer, i, dateLimit) && anyMorePages(i));
    }

    /**
     * Processes the html page to add in buffer next parsed vacancies if they're valid
     *
     * @param buffer     vacancy buffer
     * @param pageNumber html page num
     * @param dateLimit  date limiter
     * @return true if it needs to be continued on next page
     * @throws IOException if the input-output error occurs
     */
    boolean processPage(List<VacancyData> buffer, int pageNumber, LocalDateTime dateLimit) throws IOException {
        String link = buildPageLink(pageNumber);
        log.info("Trying to get access to the next page: {}", link);
        Document doc = buildDocument(link);
        Elements rows = getAllVacancyRowsOnPage(doc);
        boolean result = !rows.isEmpty(); // no more vacancies: stop vacancy processing
        for (Element row : rows) {
            if (!processRow(buffer, row, dateLimit)) {
                result = false; // datelimit: stop vacancy processing
                break;
            }
        }
        return result;
    }

    /**
     * Forms html document for further parsing
     *
     * @param url url
     * @return jsoup document
     * @throws IOException if the input-output error occurs
     */
    Document buildDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .referrer("")
                .get();
    }

    /**
     * Processes the html table row to add in buffer next parsed vacancy if its valid
     *
     * @param buffer    vacancy buffer
     * @param row       html table row
     * @param dateLimit date limiter
     * @return true if it needs to be continued on next row
     * @throws IOException if the input-output error occurs
     */
    boolean processRow(List<VacancyData> buffer, Element row, LocalDateTime dateLimit) throws IOException {
        boolean result = true;
        var date = parseDateTime(grabDateTime(row));
        if (!date.isBefore(dateLimit)) {
            grabRow(row, date).ifPresent(buffer::add);
        } else {
            result = false; // datelimit: stop vacancy processing
            log.info("The date limit has been reached ({}): {}", dateLimit, date);
        }
        return result;
    }

    /**
     * Grabs vacancy from the row if it fits the requirements
     *
     * @param row      html table row
     * @param dateTime row date
     * @return optional of vacancy if it fits
     * @throws IOException if the input-output error occurs
     */
    Optional<VacancyData> grabRow(Element row, LocalDateTime dateTime) throws IOException {
        VacancyData result = null;
        var title = composeTitle(row);
        if (titleFilter.test(title)) {
            var link = grabLink(row);
            var description = grabDescription(row);
            result = new VacancyData(title, link, description, dateTime);
            log.info("Find one more vacancy: {}", title);
        }
        return Optional.ofNullable(result);
    }

    /**
     * Composed the complex vacancy title (to prevent duplicate titles) based on vacancy's title, company, and city
     *
     * @param row vacancy row
     * @return vacancy title
     */
    String composeTitle(Element row) {
        String cityPlusCompany = StreamEx.of(grabCity(row), grabCompany(row))
                .filter(s -> !Strings.isNullOrEmpty(s))
                .joining(", ", "(", ")");
        return grabTitle(row) + (cityPlusCompany.length() > 2 ? ' ' + cityPlusCompany : "");
    }
}