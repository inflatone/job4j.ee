package ru.job4j.auto.config.helper;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.User;
import ru.job4j.auto.to.ImageTo;
import ru.job4j.auto.to.PostTo;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.converter.JsonHelper;
import ru.job4j.auto.web.converter.ModelConverter;

import static ru.job4j.auto.config.helper.BaseEntityTestHelper.setIdIfRequired;

public class ToTestHelpers {
    public static class UserToTestHelper extends BaseToTestHelper<UserTo, User> {
        public UserToTestHelper(JsonHelper jsonHelper, ModelConverter converter) {
            super(jsonHelper, UserTo.class, true, converter, "registered");
        }

        @Override
        public UserTo entityToTo(User entity) {
            return converter.asUserTo(entity, adminAccess(), selfAccess(entity.getId()));
        }
    }

    public static class PostToTestHelper extends BaseToTestHelper<PostTo, Post> {
        public PostToTestHelper(JsonHelper jsonHelper, ModelConverter converter) {
            super(jsonHelper, PostTo.class, true, converter, "posted");
        }

        @Override
        public PostTo entityToTo(Post entity) {
            return converter.asPostTo(entity, adminAccess(), selfAccess(entity.getUser().getId()), true);
        }
    }

    public static class ImageToTestHelper extends BaseToTestHelper<ImageTo, Image> {
        public ImageToTestHelper(JsonHelper jsonHelper, ModelConverter converter) {
            super(jsonHelper, ImageTo.class, false, converter);
        }

        @Override
        public ImageToTestHelper forAuth(User auth) {
            super.forAuth(auth);
            return this;
        }

        @Override
        public ResultMatcher contentJson(Image expected) {
            return contentJson(expected, getAuth().getId());
        }

        public ResultMatcher contentJson(Image expected, int userId) {
            return result -> assertMatch(readFromJsonMvcResult(result), entityToUserImageTo(result, expected, userId));
        }

        public ResultMatcher contentJson(Image expected, int userId, int postId) {
            return result -> assertMatch(readFromJsonMvcResult(result), entityToPostImageTo(result, expected, userId, postId));
        }

        private ImageTo entityToUserImageTo(MvcResult result, Image image, int userId) {
            return converter.buildImageTo(setIdIfRequired(image, result), userId, adminAccess(), selfAccess(userId));
        }

        private ImageTo entityToPostImageTo(MvcResult result, Image image, int userId, int postId) {
            return converter.buildImageTo(setIdIfRequired(image, result), userId, postId, adminAccess(), selfAccess(userId));
        }

        @Override
        public ImageTo entityToTo(Image entity) {
            throw new UnsupportedOperationException();
        }
    }
}
