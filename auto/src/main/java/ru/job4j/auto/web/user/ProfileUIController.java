package ru.job4j.auto.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.web.AuthorizedUser;

@Controller
@RequestMapping(ProfileUIController.URL)
@RequiredArgsConstructor
public class ProfileUIController {
    public static final String URL = "/profile";

    private final UserService service;

    @GetMapping
    public String getProfile(@AuthenticationPrincipal AuthorizedUser auth, ModelMap model) {
        return getProfile(auth.extract().id(), auth, model);
    }

    @GetMapping("/{id}")
    public String getProfile(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser auth, ModelMap model) {
        // actually don't need user model, but it triggers NotFoundException if non-existent user profile's requested
        model.addAttribute("user", service.find(id));

        boolean isAdmin = auth.getAuthorities().contains(Role.ADMIN);
        boolean isSelf = id == auth.extract().getId();

        model.addAttribute("profileId", id);
        model.addAttribute("editable", isAdmin || isSelf);
        model.addAttribute("modPostUrl", isSelf || !isAdmin ? "ajax/profile/posts/" : "ajax/admin/users/" + id + "/posts/");
        model.addAttribute("modImageProfileUrl", isSelf || !isAdmin ? "images/profile/" : "images/users/" + id + '/');
        model.addAttribute("modImagePostUrl", isSelf || !isAdmin ? "images/profile/posts/" : "images/users/" + id + "/posts/");
        return "profile";
    }
}
