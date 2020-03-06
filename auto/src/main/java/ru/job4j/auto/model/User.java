package ru.job4j.auto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.job4j.auto.View;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

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
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String USER_WITH_POSTS = "graph: user u join u.posts";

    public static final String BY_LOGIN = "user: findByLogin";

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(min = 4, max = 100)
    @Column(name = "login", nullable = false)
    private String login;

    @NotBlank(groups = View.Persist.class)
    @Size(min = 5, max = 100, groups = View.Persist.class)
    @JsonProperty(access = WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "registered", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Instant registered;

    @NotNull(message = "must be chosen")
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
