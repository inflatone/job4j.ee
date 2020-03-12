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
import ru.job4j.vacancy.util.ExceptionUtil;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.job4j.vacancy.util.TimeUtil.asLine;

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

    abstract String buildPageLink(int page, ParseParameters params);

    abstract Elements getAllVacancyRowsOnPage(Document doc);

    abstract String grabTitleIfValid(Element row, ParseParameters params);

    abstract String grabCity(Element row);

    abstract String grabCompany(Element row);

    abstract String grabDateTime(Element row);

    abstract ZonedDateTime parseDateTime(String dateLine);

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
    public List<VacancyData> parseVacancies(ParseParameters params) {
        List<VacancyData> vacancies = new ArrayList<>();
        ExceptionUtil.handleRun(() -> mainLoop(vacancies, params));
        log.info("Found and parsed {} vacancy(ies) between {}", vacancies.size(), params.getDateRangeAsLine());
        return vacancies;
    }

    /**
     * Represents main loop of vacancy parser that processes page after page of the job-offers sql.ru forum section
     *
     * @param buffer vacancy buffer
     * @param params parse params
     * @throws IOException if the input-output error occurs
     */
    void mainLoop(List<VacancyData> buffer, ParseParameters params) throws IOException {
        int i = 0;
        do {
            i++;
        } while (processPage(buffer, i, params) && anyMorePages(i));
    }

    /**
     * Processes the html page to add in buffer next parsed vacancies if they're valid
     *
     * @param buffer     vacancy buffer
     * @param pageNumber html page num
     * @param params     parse params
     * @return true if it needs to be continued on next page
     * @throws IOException if the input-output error occurs
     */
    boolean processPage(List<VacancyData> buffer, int pageNumber, ParseParameters params) throws IOException {
        String link = buildPageLink(pageNumber, params);
        log.info("Trying to get access to the next page: {}", link);
        Document doc = buildDocument(link);
        Elements rows = getAllVacancyRowsOnPage(doc);
        boolean result = !rows.isEmpty(); // no more vacancies: stop vacancy processing
        for (Element row : rows) {
            if (!processRow(buffer, row, params)) {
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
     * @param buffer vacancy buffer
     * @param row    html table row
     * @param params parse params
     * @return true if it needs to be continued on next row
     * @throws IOException if the input-output error occurs
     */
    boolean processRow(List<VacancyData> buffer, Element row, ParseParameters params) throws IOException {
        var date = parseDateTime(grabDateTime(row));
        if (params.isReachLimit(date)) {
            log.info("The date limit has been reached ({}): {}", asLine(params.getStart()), asLine(date));
            return false;
        }
        if (params.isInRange(date)) {
            grabRow(row, date, params).ifPresent(buffer::add);
        }
        return true;
    }

    /**
     * Grabs vacancy from the row if it fits the requirements
     *
     * @param row      html table row
     * @param dateTime row date
     * @param params   parse params
     * @return optional of vacancy if it fits
     * @throws IOException if the input-output error occurs
     */
    Optional<VacancyData> grabRow(Element row, ZonedDateTime dateTime, ParseParameters params) throws IOException {
        VacancyData result = null;
        var title = checkAndComposeTitle(row, params);
        if (title != null) {
            var link = grabLink(row);
            var description = grabDescription(row);
            result = new VacancyData(title, link, description, dateTime);
            log.info("Find one more vacancy: {}", title);
        }
        return Optional.ofNullable(result);
    }

    /**
     * Composes the complex vacancy title (to prevent duplicate titles) based on vacancy's title, company, and city
     * Validates the result to be fitted with the given parse params as well
     *
     * @param row vacancy row
     * @return vacancy title or null if it's not valid
     */
    String checkAndComposeTitle(Element row, ParseParameters params) {
        var title = grabTitleIfValid(row, params);
        if (title == null) {
            return null;
        }
        String cityPlusCompany = StreamEx.of(grabCity(row), grabCompany(row))
                .filter(s -> !Strings.isNullOrEmpty(s))
                .joining(", ", "(", ")");
        return title + (cityPlusCompany.length() > 2 ? ' ' + cityPlusCompany : "");
    }
}