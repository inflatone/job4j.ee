package ru.job4j.auto.to;

import lombok.Getter;
import ru.job4j.auto.model.Car;

import java.net.URI;
import java.time.Instant;

@Getter
public class PostTo extends BaseTo {
    private final String title;

    private final String message;

    private final Instant posted;

    private final boolean completed;

    private final Integer price;

    private final Car car;

    private final UserTo user;

    private final ImageTo image;

    public PostTo(String title, String message, Instant posted, boolean completed, Integer price, Car car, UserTo user,
                  ImageTo image, URI url, URI urlToModify) {
        super(url, urlToModify);
        this.title = title;
        this.message = message;
        this.posted = posted;
        this.completed = completed;
        this.price = price;
        this.car = car;
        this.user = user;
        this.image = image;
    }
}
