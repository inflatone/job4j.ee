package ru.job4j.auto.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RootController {
    @GetMapping("/")
    public String root(RedirectAttributes ra) {
        return "redirect:posts";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/users")
    public String getUsers(@AuthenticationPrincipal AuthorizedUser auth) {
        return auth.isAdmin() ? "users" : "redirect:profile";
    }

    @GetMapping("/posts")
    public String getPosts() {
        return "posts";
    }
}
