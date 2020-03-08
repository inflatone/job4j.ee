package ru.job4j.auto.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String root() {
        return "redirect:posts";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getUsers() {
        return "users";
    }

    @GetMapping("/posts")
    public String getPosts(@AuthenticationPrincipal AuthorizedUser auth, ModelMap model) {
        model.addAttribute("profileId", auth.extract().id());
        return "posts";
    }
}
