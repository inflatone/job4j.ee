package ru.job4j.auto.web.converter;

import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.model.User;
import ru.job4j.auto.to.ImageTo;
import ru.job4j.auto.to.PostTo;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.AuthorizedUser;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModelConverter {
    public static final String USER_IMAGE_NAME = "userPhoto";
    public static final String POST_IMAGE_NAME = "postPhoto";

    private final UrlConverter urlConverter;

    public UserTo asUserTo(User user, AuthorizedUser auth) {
        return asUserTo(user, auth.id(), auth.getAuthorities().contains(Role.ADMIN));
    }

    public List<UserTo> asUserTo(List<User> users, AuthorizedUser auth) {
        boolean asAdmin = auth.getAuthorities().contains(Role.ADMIN);
        return StreamEx.of(users).map(u -> asUserTo(u, auth.id(), asAdmin)).toList();
    }

    public UserTo asUserTo(User user, int authId, boolean adminAccess) {
        Integer id = user.getId();
        boolean selfAccess = authId == id;
        return new UserTo(id, user.getName(), user.getLogin(), user.getRegistered(), user.isEnabled(), user.getRole(),
                buildImageTo(user, adminAccess, selfAccess),
                urlConverter.buildUserUrl(id),
                urlConverter.buildUserModifyUrl(id, adminAccess, selfAccess),
                urlConverter.buildAllPostsUrl(id, adminAccess, selfAccess),
                urlConverter.buildUserAddPostUrl(adminAccess ? id : null));
    }

    public UserTo asBasicUserTo(User user, boolean withUser) {
        return new UserTo(user.getId(), withUser ? user.getName() : null, withUser ? user.getLogin() : null, null, null, null,
                null, urlConverter.buildUserUrl(user.getId()), null, null, null);
    }

    public List<PostTo> asPostTo(List<Post> posts, AuthorizedUser auth) {
        return asPostTo(posts, auth, false);
    }

    public List<PostTo> asPostTo(List<Post> posts, AuthorizedUser auth, boolean withUser) {
        boolean asAdmin = auth.getAuthorities().contains(Role.ADMIN);
        return StreamEx.of(posts).map(p -> asPostTo(p, auth.id(), asAdmin, withUser)).toList();
    }

    public PostTo asPostTo(Post post, AuthorizedUser auth) {
        return asPostTo(post, auth, false);
    }

    public PostTo asPostTo(Post post, AuthorizedUser auth, boolean withUser) {
        boolean isAdmin = auth.getAuthorities().contains(Role.ADMIN);
        return asPostTo(post, auth.id(), isAdmin, withUser);
    }

    private PostTo asPostTo(Post post, int authId, boolean adminAccess, boolean withUser) {
        Integer id = post.getId();
        Integer userId = post.getUser().getId();
        boolean selfAccess = authId == userId;
        return new PostTo(post.getTitle(), post.getMessage(), post.getPosted(), post.isCompleted(),
                post.getPrice(), post.getCar(), asBasicUserTo(post.getUser(), withUser),
                buildImageTo(post, adminAccess, selfAccess),
                urlConverter.buildPostUrl(id),
                urlConverter.buildPostModifyUrl(userId, id, adminAccess, selfAccess));
    }

    public ImageTo buildImageTo(User user, boolean adminAccess, boolean selfAccess) {
        Image image = user.getImage();
        return new ImageTo(USER_IMAGE_NAME, image == null,
                urlConverter.imageUrl(image == null ? null : image.getId()),
                selfAccess || adminAccess ? urlConverter.userImageModifiableUrl(selfAccess ? null : user.getId()) : null);
    }

    public ImageTo buildImageTo(Post post, boolean adminAccess, boolean selfAccess) {
        Image image = post.getImage();
        return new ImageTo(POST_IMAGE_NAME, image == null,
                urlConverter.imageUrl(image == null ? null : image.getId()),
                (selfAccess || adminAccess)
                        ? urlConverter.postImageModifiableUrl(selfAccess ? null : post.getUser().getId(), post.getId())
                        : null);
    }
}
