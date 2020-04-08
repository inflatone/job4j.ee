package ru.job4j.auto.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.job4j.auto.model.Car;

import java.net.URI;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class PostTo extends BaseTo {
    private String title;

    private String message;

    private Instant posted;

    private boolean completed;

    private Integer price;

    private Car car;

    private UserTo user;

    private ImageTo image;

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
