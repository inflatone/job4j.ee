package ru.job4j.auto.web;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import ru.job4j.auto.ActiveDbProfileResolver;
import ru.job4j.auto.SpringMvcTestConfig;
import ru.job4j.auto.model.User;

import javax.annotation.PostConstruct;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @PostConstruct
    private void postConstruct() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(CHARACTER_ENCODING_FILTER)
                .build();
    }

    public ResultActions perform(RequestWrapper wrapper) throws Exception {
        return perform(wrapper.builder);
    }

    public ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
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

    protected RequestWrapper doDelete() {
        return wrap(delete(url));
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
                    .param("role", user.getRole().name());
            return this;
        }
    }

    public static ResultMatcher contentTypeIsJson() {
        return content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);
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