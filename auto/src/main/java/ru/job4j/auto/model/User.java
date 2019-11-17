package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "login", name = "users_unique_login_idx")})
@NamedEntityGraphs({
        @NamedEntityGraph(name = User.USER_WITH_POSTS, attributeNodes = @NamedAttributeNode(value = "posts", subgraph = "user.posts"),
                subgraphs = @NamedSubgraph(name = "user.posts", attributeNodes = @NamedAttributeNode(value = "car")))
})
@NamedQueries({
        @NamedQuery(name = User.BY_LOGIN, query = "SELECT u FROM User u WHERE u.login=:login")
})
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    public static final String USER_WITH_POSTS = "graph: user u join u.posts";

    public static final String BY_LOGIN = "user: findByLogin";

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "registered", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Instant registered;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;

    @BatchSize(size = 200)
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Post> posts;

    public User(Integer id, String name, String login, String password, Role role) {
        super(id);
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
