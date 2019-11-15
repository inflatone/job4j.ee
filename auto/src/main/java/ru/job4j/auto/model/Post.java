package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseEntity {
    private String title;

    private String message;

    private Instant posted;

    private Integer price;

    private Car car;

    private User user;

    public Post(Integer id, String title, String message, Instant posted, Integer price, Car car, User user) {
        super(id);
        this.title = title;
        this.message = message;
        this.posted = posted;
        this.price = price;
        this.car = car;
        this.user = user;
    }
}
