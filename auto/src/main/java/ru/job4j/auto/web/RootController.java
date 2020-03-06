package ru.job4j.auto.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RootController {
    @GetMapping("/")
    public String root() {
        return "redirect:profile";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String getProfile() {
        return "profile";
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getProfile(@PathVariable int id, ModelMap model) {
        model.addAttribute("profileId", id);
        return "profile";
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getUsers() {
        return "users";
    }
}
