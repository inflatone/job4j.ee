package ru.job4j.auto.web.converter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.auto.web.ImageController;
import ru.job4j.auto.web.post.AdminPostController;
import ru.job4j.auto.web.post.PostController;
import ru.job4j.auto.web.post.ProfilePostController;
import ru.job4j.auto.web.user.AdminController;
import ru.job4j.auto.web.user.ProfileController;
import ru.job4j.auto.web.user.UserController;

import java.net.URI;

@Component
public class UrlConverter {
    public URI buildUrl(String url, Object... uriVariables) {
        return builder().path(url).build(uriVariables);
    }

    public URI buildUrl(String root, String suffix, Object... uriVariables) {
        return buildUrl(root + '/' + suffix, uriVariables);
    }

    public URI buildUserUrl(int id) {
        return builder().path(UserController.URL + "/{id}")
                .build(id);
    }

    public URI buildUserModifyUrl(int id, boolean adminAccess, boolean selfAccess) {
        return adminAccess ? builder().path(AdminController.URL + "/{id}").buildAndExpand(id).toUri()
                : selfAccess ? builder().path(ProfileController.URL).build().toUri() : null;
    }

    public URI buildAllPostsUrl(int userId, boolean adminAccess, boolean selfAccess) {
        return selfAccess ? builder().path(ProfilePostController.URL).build().toUri()
                : adminAccess ? builder().path(AdminPostController.URL).build(userId) : buildAllPostsUrl(userId);
    }

    public URI buildAllPostsUrl(Integer userId) {
        return userId == null ? builder().path(PostController.URL).build().toUri()
                : builder().path(PostController.URL + "/filter").queryParam("user", "{id}").build(userId);
    }

    public URI buildUserAddPostUrl(Integer userId) {
        return userId != null ? builder().path(AdminPostController.URL).build(userId)
                : builder().path(ProfilePostController.URL).build().toUri();
    }

    public URI buildPostUrl(int postId) {
        return builder()
                .path(PostController.URL + "/{id}")
                .build(postId);
    }

    public URI buildPostModifyUrl(int userId, int postId, boolean adminAccess, boolean selfAccess) {
        return adminAccess || selfAccess ? buildPostModifyUrl(selfAccess ? null : userId, postId) : null;
    }

    private URI buildPostModifyUrl(Integer userId, int id) {
        return userId == null ? builder().path(ProfilePostController.URL + "/{id}").buildAndExpand(id).toUri()
                : builder().path(AdminPostController.URL + "/{id}").buildAndExpand(userId, id).toUri();
    }

    public URI imageUrl(Integer imageId) {
        return imageId == null ? imageUrl() : builder()
                .path((ImageController.URL + "/{id}"))
                .buildAndExpand(imageId)
                .toUri();
    }

    private URI imageUrl() {
        return builder().path(ImageController.URL + '/').build().toUri();
    }

    public URI userImageModifiableUrl(Integer id) {
        return id == null ? builder().path(ImageController.URL + "/profile").build().toUri()
                : builder().path(ImageController.URL + "/users/{id}").buildAndExpand(id).toUri();
    }

    public URI postImageModifiableUrl(Integer userId, int id) {
        return userId == null ? builder().path(ImageController.URL + "/profile/posts/{id}").buildAndExpand(id).toUri()
                : builder().path(ImageController.URL + "/users/{userId}/posts/{id}").buildAndExpand(userId, id).toUri();
    }

    private ServletUriComponentsBuilder builder() {
        return ServletUriComponentsBuilder.fromCurrentContextPath();
    }
}
