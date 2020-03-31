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
        return asUserTo(user, auth.getAuthorities().contains(Role.ADMIN), auth.id() == user.getId());
    }

    public List<UserTo> asUserTo(List<User> users, AuthorizedUser auth) {
        boolean asAdmin = auth.getAuthorities().contains(Role.ADMIN);
        return StreamEx.of(users).map(u -> asUserTo(u, asAdmin, auth.id() == u.getId())).toList();
    }

    public UserTo asUserTo(User user, boolean adminAccess, boolean selfAccess) {
        Integer id = user.getId();
        return new UserTo(id, user.getName(), user.getLogin(), user.getRegistered(), user.isEnabled(), user.getRole(),
                buildImageTo(user, adminAccess, selfAccess),
                urlConverter.buildUserUrl(id),
                urlConverter.buildUserModifyUrl(id, adminAccess, selfAccess),
                urlConverter.buildAllPostsUrl(id, adminAccess, selfAccess),
                urlConverter.buildUserAddPostUrl(adminAccess ? id : null));
    }

    public UserTo asBasicUserTo(User user, boolean includeUserData) {
        return new UserTo(user.getId(), includeUserData ? user.getName() : null, includeUserData ? user.getLogin() : null, null, null, null,
                null, urlConverter.buildUserUrl(user.getId()), null, null, null);
    }

    public List<PostTo> asPostTo(List<Post> posts, AuthorizedUser auth, boolean includeUserData) {
        boolean asAdmin = auth.getAuthorities().contains(Role.ADMIN);
        return StreamEx.of(posts).map(p -> asPostTo(p, asAdmin, auth.id() == p.getUser().getId(), includeUserData)).toList();
    }

    public PostTo asPostTo(Post post, AuthorizedUser auth) {
        return asPostTo(post, auth, false);
    }

    public PostTo asPostTo(Post post, AuthorizedUser auth, boolean includeUserData) {
        boolean isAdmin = auth.getAuthorities().contains(Role.ADMIN);
        return asPostTo(post, isAdmin, auth.id() == post.getUser().getId(), includeUserData);
    }

    public PostTo asPostTo(Post post, boolean adminAccess, boolean selfAccess, boolean includeUserData) {
        Integer id = post.getId();
        Integer userId = post.getUser().getId();
        return new PostTo(post.getTitle(), post.getMessage(), post.getPosted(), post.isCompleted(),
                post.getPrice(), post.getCar(), asBasicUserTo(post.getUser(), includeUserData),
                buildImageTo(post, adminAccess, selfAccess),
                urlConverter.buildPostUrl(id),
                urlConverter.buildPostModifyUrl(userId, id, adminAccess, selfAccess));
    }

    public ImageTo buildImageTo(User user, boolean adminAccess, boolean selfAccess) {
        return buildImageTo(user.getImage(), user.getId(), adminAccess, selfAccess);
    }

    public ImageTo buildImageTo(Image image, int userId, boolean adminAccess, boolean selfAccess) {
        return new ImageTo(USER_IMAGE_NAME, image == null,
                urlConverter.imageUrl(image == null ? null : image.getId()),
                selfAccess || adminAccess ? urlConverter.userImageModifiableUrl(selfAccess ? null : userId) : null);
    }

    public ImageTo buildImageTo(Post post, boolean adminAccess, boolean selfAccess) {
        return buildImageTo(post.getImage(), post.getUser().getId(), post.getId(), adminAccess, selfAccess);
    }

    public ImageTo buildImageTo(Image image, int userId, int postId, boolean adminAccess, boolean selfAccess) {
        return new ImageTo(POST_IMAGE_NAME, image == null,
                urlConverter.imageUrl(image == null ? null : image.getId()),
                (selfAccess || adminAccess)
                        ? urlConverter.postImageModifiableUrl(selfAccess ? null : userId, postId)
                        : null);
    }
}
