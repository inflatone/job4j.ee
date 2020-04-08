package ru.job4j.auto.config.helper;

import one.util.streamex.StreamEx;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.web.converter.JsonHelper;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImageEntityTestHelper extends BaseEntityTestHelper<Image> {
    private static final Pattern FILENAME_REGEX = Pattern.compile("(?:filename=\")(.*)(?:\")");

    private final Random random = new Random();

    private final Image defaultImage;

    public ImageEntityTestHelper(JsonHelper jsonHelper, Image defaultImage) {
        super(jsonHelper, Image.class, false);
        this.defaultImage = defaultImage;
    }

    public void assertDefault(Image actual) {
        assertMatch(actual, defaultImage);
    }

    public ResultMatcher contentImage(Image expected) {
        return result -> assertMatch(retrieveImage(result), expected, "id");
    }

    public ResultMatcher contentDefaultImage() {
        return contentImage(defaultImage);
    }

    @Override
    public ResultMatcher contentJson(Image expected) {
        return result -> assertJsonImage(result, expected);
    }

    private void assertJsonImage(MvcResult result, Image expected) throws UnsupportedEncodingException {
        Image actual = readFromJsonMvcResult(result);
        assertNotNull(actual.getId(), "Image id must be inserted");
        assertMatch(actual, expected, "id", "data");
    }

    private static Image retrieveImage(MvcResult result) {
        var response = result.getResponse();
        return new Image(null, retrieveFilename(response), response.getContentType(), response.getContentAsByteArray());
    }

    private static String retrieveFilename(MockHttpServletResponse response) {
        return StreamEx.of(response.getHeaderValues("Content-Disposition"))
                .map(v -> {
                    var m = FILENAME_REGEX.matcher((String) v);
                    return m.find() ? m.group(1) : null;
                })
                .findFirst(Objects::nonNull)
                .orElseThrow(() -> new AssertionError("Filename of image must be set in response"));
    }

    @Override
    protected Image doCopy(Image entity) {
        return new Image(null, entity.getFileName(), entity.getContentType(), Arrays.copyOf(entity.getData(), entity.getData().length));
    }

    @Override
    public Image newEntity() {
        byte[] data = new byte[10000];
        random.nextBytes(data);
        return new Image(null, "Other.jpg", MediaType.IMAGE_JPEG_VALUE, data);

    }

    @Override
    public Image editedEntity(Image image) {
        var edited = doCopy(image);
        edited.setFileName("edited.jpg");
        edited.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        random.nextBytes(edited.getData());
        return edited;
    }
}
