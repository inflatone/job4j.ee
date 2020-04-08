package ru.job4j.auto.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.job4j.auto.model.Role;

import java.net.URI;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class UserTo extends BaseTo {
    private int id;

    private String name;

    private String login;

    private Instant registered;

    private Boolean enabled;

    private Role role;

    private ImageTo image;

    private URI urlToPosts;

    private URI urlToAddPost;

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
