package ru.job4j.auto.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.auto.model.*;
import ru.job4j.auto.service.DataService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = DataController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DataController {
    public static final String AJAX_URL = "/ajax";

    static final String URL = AJAX_URL + "/data";

    private final DataService service;

    @GetMapping("/roles")
    public Map<String, Role> roles() {
        log.info("Return available roles");
        var auth = SecurityHelper.safeGet();
        return service.findAvailableRoles(auth == null ? Role.USER : auth.extract().getRole());
    }

    @GetMapping("/vendors")
    public Map<Integer, Vendor> vendors() {
        log.info("Return available vendors");
        return service.findAvailableVendors();
    }

    @GetMapping("/bodies")
    public Map<Integer, Body> bodies() {
        log.info("Return available bodies");
        return service.findAvailableCarBodies();
    }

    @GetMapping("/engines")
    public Map<Integer, Engine> engines() {
        log.info("Return available engines");
        return service.findAvailableCarEngines();
    }

    @GetMapping("/transmissions")
    public Map<Integer, Transmission> transmissions() {
        log.info("Return available transmissions");
        return service.findAvailableCarTransmissions();
    }
}
