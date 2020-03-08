package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "post")
@NamedEntityGraphs({@NamedEntityGraph(name = Post.POST_WITH_CAR, attributeNodes = {@NamedAttributeNode(value = "car", subgraph = "car.details")},
        subgraphs = @NamedSubgraph(name = "car.details", attributeNodes = {@NamedAttributeNode(value = "vendor"),
                @NamedAttributeNode(value = "body"), @NamedAttributeNode(value = "engine"), @NamedAttributeNode(value = "transmission")}))})
@NamedQueries({
        @NamedQuery(name = Post.FIND, query = "SELECT p FROM Post p JOIN FETCH p.car c WHERE p.id=:id AND p.user.id=:profileId"),
        @NamedQuery(name = Post.FIND_ALL, query = "SELECT p FROM Post p JOIN FETCH p.car c WHERE p.user.id=:profileId"),
        @NamedQuery(name = Post.DELETE, query = "DELETE FROM Post p WHERE p.id=:id AND p.user.id=:profileId")})
@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseEntity {
    public static final String POST_WITH_CAR = "graph: post p join p.car";

    public static final String FIND = "post: find";
    public static final String FIND_ALL = "post: findAll";
    public static final String DELETE = "post: delete";

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 2000)
    @Column(name = "message")
    private String message;

    @Column(name = "posted", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Instant posted;

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private boolean completed;

    @Column(name = "price")
    private Integer price;

    @Valid
    @OneToOne(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REMOVE})
    @JoinColumn(name = "car_id", unique = true)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = PERSIST, orphanRemoval = true)
    @JoinColumn(name = "image_id", unique = true)
    private Image image;

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
