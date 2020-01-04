package ru.job4j.ee.store.web;

import org.slf4j.Logger;
import ru.job4j.ee.store.model.BaseNamedEntity;
import ru.job4j.ee.store.model.Country;
import ru.job4j.ee.store.service.CityService;
import ru.job4j.ee.store.util.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.service.CityService.getCityService;
import static ru.job4j.ee.store.web.AjaxServlet.Data;
import static ru.job4j.ee.store.web.AjaxServlet.Data.*;
import static ru.job4j.ee.store.web.auth.AuthUtil.composeAvailableRoles;
import static ru.job4j.ee.store.web.json.JsonUtil.*;

/**
 * Represents web layer of the app that serves additional data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-25
 */
public class AjaxServlet extends DispatcherServlet<Data> {
    private static final Logger log = getLogger(AjaxServlet.class);

    private CityService cityService = getCityService();

    public AjaxServlet() {
        super(Data.class, null);
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(roles, this::roles);
        submitGetAction(cities, this::cities);
        submitGetAction(countries, this::countries);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(countries, this::updateCountries);
    }

    /**
     * Writes to the response the set of available roles (in dependence of request parameters) as JSON object
     *
     * @param request  request
     * @param response response
     */
    private void roles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Get roles to form");
        var roles = composeAvailableRoles(request);
        asJsonToResponse(response, roles);
    }

    /**
     * Writes to the response city data (in dependence of request parameters) as JSON object
     *
     * @param request  request
     * @param response response
     */
    private void cities(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var countryId = ServletUtil.getRequiredId(request);
        log.info("Get cities of country with id={}", countryId);
        var cities = cityService.findByCountryId(countryId);
        asJsonToResponse(response, cities);
    }

    private void countries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Get all countries");
        var countries = cityService.findAllCountries();
        asJsonToResponse(response, countries);
    }

    private void updateCountries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var action = ServletUtil.getRequiredParameter(request, "action", Function.identity());
        if ("clear".equals(action)) {
            log.info("Clear unused countries");
            int amount = cityService.clearCountries();
            asJsonToResponse(response, "message", amount + " countries removed");
        } else if ("update".equals(action)) {
            log.info("Update country list");
            var countriesTo = listFromJson(request.getReader(), CountryTo.class);
            List<Country> saved = cityService.saveAllCountries(countriesTo);
            asJsonToResponse(response, "message", countriesTo.size() + " countries received, " + saved.size() + " countries saved");
        } else {
            errorAsJsonToResponse(response, "Wrong action has been chosen, please try again");
        }
    }

    public static class CountryTo extends BaseNamedEntity {
        private String code;

        CountryTo() {
        }

        CountryTo(String name) {
            super(null, name);
        }

        public static Country fromTo(CountryTo to) {
            return new Country(to.getName());
        }

    }

    enum Data {
        roles,
        cities,
        countries
    }
}
