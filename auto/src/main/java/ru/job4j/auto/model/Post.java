package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "post")
@NamedEntityGraphs({@NamedEntityGraph(name = Post.POST_WITH_CAR, attributeNodes = {@NamedAttributeNode("car")})})
@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseEntity {
    public static final String POST_WITH_CAR = "graph: post p join p.car";

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "posted", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Instant posted;

    @Column(name = "price")
    private Integer price;

    @OneToOne(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REMOVE})
    @JoinColumn(name = "car_id", unique = true)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
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
