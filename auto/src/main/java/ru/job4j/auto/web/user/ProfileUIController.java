package ru.job4j.auto.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.web.AuthorizedUser;
import ru.job4j.auto.web.converter.UrlConverter;

@Controller
@RequestMapping(ProfileUIController.URL)
@RequiredArgsConstructor
public class ProfileUIController {
    public static final String URL = "/profile";

    private final UserService service;

    private final UrlConverter urlConverter;

    @GetMapping
    public String getProfile(@AuthenticationPrincipal AuthorizedUser auth, ModelMap model) {
        model.addAttribute("userDataUrl",
                urlConverter.buildUserUrl(auth.id()));
        return "profile";
    }

    @GetMapping("/{id}")
    public String getProfile(@PathVariable int id, ModelMap model) {
        // actually we don't need user model now (it's AJAXed to fill page separately),
        // but it triggers NotFoundException throwing if non-existent user profile page's requested
        model.addAttribute("user", service.find(id));
        model.addAttribute("id", id);
        return "profile";
    }
}
