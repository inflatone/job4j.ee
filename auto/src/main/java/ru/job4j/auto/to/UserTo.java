package ru.job4j.auto.to;

import lombok.Getter;
import ru.job4j.auto.model.Role;

import java.net.URI;
import java.time.Instant;

@Getter
public class UserTo extends BaseTo {
    private final int id;

    private final String name;

    private final String login;

    private final Instant registered;

    private final Boolean enabled;

    private final Role role;

    private final ImageTo image;

    private final URI urlToPosts;

    private final URI urlToAddPost;

    public UserTo(int id, String name, String login, Instant registered, Boolean enabled,
                  Role role, ImageTo image, URI url, URI urlToModify, URI urlToPosts, URI urlToAddPost) {
        super(url, urlToModify);
        this.id = id;
        this.name = name;
        this.login = login;
        this.registered = registered;
        this.enabled = enabled;
        this.role = role;
        this.image = image;
        this.urlToPosts = urlToPosts;
        this.urlToAddPost = urlToAddPost;
    }
}
