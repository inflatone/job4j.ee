package ru.job4j.auto.web;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import ru.job4j.auto.ActiveDbProfileResolver;
import ru.job4j.auto.SpringMvcTestConfig;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.User;
import ru.job4j.auto.web.converter.JsonHelper;

import javax.annotation.PostConstruct;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static ru.job4j.auto.web.AbstractControllerTest.RequestWrapper.wrap;

@Transactional
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@WebAppConfiguration
@SpringJUnitWebConfig(classes = SpringMvcTestConfig.class)
public abstract class AbstractControllerTest {
    private static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER = new CharacterEncodingFilter();

    static {
        CHARACTER_ENCODING_FILTER.setEncoding(UTF_8.name());
        CHARACTER_ENCODING_FILTER.setForceEncoding(true);
    }

    protected final String url;

    public AbstractControllerTest(String url) {
        this.url = url + '/';
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static JsonHelper jsonHelper;

    @PostConstruct
    private void postConstruct() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(CHARACTER_ENCODING_FILTER)
                .apply(springSecurity())
                .build();
        jsonHelper = webApplicationContext.getBean(JsonHelper.class);

    }

    public ResultActions perform(RequestWrapper wrapper) throws Exception {
        return perform(wrapper.builder);
    }

    public ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder.with(csrf()));
    }

    protected RequestWrapper doGet(String urlTemplatePad, Object... uriVars) {
        return wrap(get(url + urlTemplatePad, uriVars));
    }

    protected RequestWrapper doGet() {
        return wrap(get(url));
    }

    protected RequestWrapper doGet(String urlTemplatePad) {
        return wrap(get(url + urlTemplatePad));
    }

    protected RequestWrapper doGet(int id) {
        return doGet("{id}", id);
    }

    protected RequestWrapper doPost(String urlTemplatePad, Object... uriVars) {
        return wrap(post(url + urlTemplatePad, uriVars));
    }

    protected RequestWrapper doPost(int id) {
        return wrap(post(url + "{id}", id));
    }

    protected RequestWrapper doPost() {
        return wrap(post(url));
    }

    protected RequestWrapper doPut(String urlTemplatePad, Object... uriVars) {
        return wrap(put(url + urlTemplatePad, uriVars));
    }

    protected RequestWrapper doPut(int id, String property) {
        return doPut("{id}/" + property, id);
    }

    protected RequestWrapper doDelete() {
        return wrap(delete(url));
    }

    protected RequestWrapper doDelete(String urlTemplatePad, Object... uriVars) {
        return wrap(delete(url + urlTemplatePad, uriVars));
    }

    protected RequestWrapper doDelete(int id) {
        return wrap(delete(url + "{id}", id));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RequestWrapper {
        private final MockHttpServletRequestBuilder builder;

        public static RequestWrapper wrap(MockHttpServletRequestBuilder builder) {
            return new RequestWrapper(builder);
        }

        public MockHttpServletRequestBuilder unwrap() {
            return builder;
        }

        public RequestWrapper userAsFormData(User user) {
            builder.param("id", user.getId() == null ? null : String.valueOf(user.getId()))
                    .param("login", user.getLogin())
                    .param("password", user.getPassword())
                    .param("name", user.getName())
                    .param("role", user.getRole().getAuthority());
            return this;
        }

        public RequestWrapper postAsFormData(Post post) {
            var car = post.getCar();
            builder.param("id", post.getId() == null ? null : String.valueOf(post.getId()))
//                    .param("user.id", String.valueOf(post.getUser().getId()))
                    .param("title", post.getTitle())
                    .param("price", post.getPrice() == null ? null : String.valueOf(post.getPrice()))
                    .param("message", post.getMessage())
                    .param("car.id", car.getId() == null ? null : String.valueOf(car.getId()))
                    .param("car.vendor.id", String.valueOf(car.getVendor().getId()))
                    .param("car.model", car.getModel())
                    .param("car.year", String.valueOf(car.getYear()))
                    .param("car.body.id", String.valueOf(car.getBody().getId()))
                    .param("car.engine.id", String.valueOf(car.getEngine().getId()))
                    .param("car.transmission.id", String.valueOf(car.getTransmission().getId()))
                    .param("car.mileage", String.valueOf(car.getMileage()));
            return this;
        }

        public <T> RequestWrapper jsonBody(T body) {
            builder.contentType(MediaType.APPLICATION_JSON).content(jsonHelper.asJson(body));
            return this;
        }


        public RequestWrapper attachImage(String name, Image image) {
            if (builder.getClass() != MockMultipartHttpServletRequestBuilder.class) {
                throw new IllegalStateException("This RequestWrapper doesn't support multipart");
            }
            var mockMultipartFile = new MockMultipartFile(name, image.getFileName(), image.getContentType(), image.getData());
            ((MockMultipartHttpServletRequestBuilder) builder).file(mockMultipartFile);
            return this;
        }

        public RequestWrapper auth(User user) {
            builder.with(authentication(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword())));
            return this;
        }
    }

    public static ResultMatcher contentTypeIsJson() {
        return content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);
    }

    public static ResultMatcher contentTypeIsJpg() {
        return content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG);
    }

    public static int getCreatedResourceId(MvcResult result) {
        var location = (result.getResponse().getHeader("Location"));
        assertNotNull(location, "Location must be contained in response");
        try {
            return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("No found ID in response");
        }

    }
}
