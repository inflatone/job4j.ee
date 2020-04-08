package ru.job4j.auto.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.auto.model.BaseEntity;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.service.DataService;
import ru.job4j.auto.web.converter.UrlConverter;
import ru.job4j.auto.web.user.AdminController;
import ru.job4j.auto.web.user.ProfileController;
import ru.job4j.auto.web.user.UserController;

import java.net.URI;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = DataController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DataController {
    public static final String AJAX_URL = "/ajax";

    static final String URL = AJAX_URL + "/data";

    private final DataService service;

    private final UrlConverter urlConverter;

    @GetMapping
    public Map<String, URI> data() {
        return Map.of(
                "login", urlConverter.buildUrl(URL, "login/"),
                "users", urlConverter.buildUrl(URL, "users/"),
                "posts", urlConverter.buildUrl(URL, "posts/"),
                "profile", urlConverter.buildUrl(URL, "profile/")
        );
    }

    @GetMapping("/login")
    public Map<String, URI> loginPage() {
        return Map.of(
                "urlToLogin", urlConverter.buildUrl("/security"),
                "urlToRegister", urlConverter.buildUrl(ProfileController.URL, "registration"),
                "urlToRoles", urlConverter.buildUrl(URL, "roles")
        );
    }

    @GetMapping("/users")
    public Map<String, URI> usersPage(@AuthenticationPrincipal AuthorizedUser auth) {
        return auth.isAdmin() ? usersPageAsAdmin() : usersPage();
    }

    private Map<String, URI> usersPage() {
        return Map.of("urlToRoles", urlConverter.buildUrl(URL, "roles"),
                "urlToUsers", urlConverter.buildUrl(UserController.URL));
    }

    private Map<String, URI> usersPageAsAdmin() {
        return Map.of("urlToRoles", urlConverter.buildUrl(URL, "roles"),
                "urlToUsers", urlConverter.buildUrl(UserController.URL),
                "urlToAddUser", urlConverter.buildUrl(AdminController.URL));
    }

    @GetMapping("/posts")
    public Map<String, URI> postsPage(@AuthenticationPrincipal AuthorizedUser auth) {
        return Map.of("urlToUser", urlConverter.buildUserUrl(auth.id()),
                "urlToPosts", urlConverter.buildAllPostsUrl(null),
                "urlToAddPost", urlConverter.buildUserAddPostUrl(null),
                "urlToCarDetails", urlConverter.buildUrl(URL, "details"));
    }

    @GetMapping("/profile")
    public Map<String, URI> profilePage(@AuthenticationPrincipal AuthorizedUser auth) {
        return profilePage(auth.id());
    }

    @GetMapping("/profile/{id}")
    public Map<String, URI> profilePage(@PathVariable int id) {
        return Map.of("userDataUrl", urlConverter.buildUserUrl(id),
                "urlToRoles", urlConverter.buildUrl(URL, "roles"),
                "urlToCarDetails", urlConverter.buildUrl(URL, "details"));
    }

    @GetMapping("/roles")
    public Map<String, Role> roles() {
        log.info("Return available roles");
        var auth = SecurityHelper.safeGet();
        return service.findAvailableRoles(auth == null ? Role.USER : auth.extract().getRole());
    }

    @GetMapping("/details")
    public Map<String, Map<Integer, ? extends BaseEntity>> details() {
        log.info("Return all available car details");
        return Map.of("vendors", service.findAvailableVendors(),
                "bodies", service.findAvailableCarBodies(),
                "engines", service.findAvailableCarEngines(),
                "transmissions", service.findAvailableCarTransmissions());
    }
}
