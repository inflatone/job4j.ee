package ru.job4j.auto.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.service.DataService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = DataController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DataController {
    static final String URL = "/ajax/data";

    private final DataService service;

    @GetMapping("/roles")
    public Map<String, Role> roles() {
        log.info("Return available roles");
        var auth = SecurityHelper.safeGet();
        return service.findAvailableRoles(auth == null ? Role.USER : auth.extract().getRole());
    }
}
