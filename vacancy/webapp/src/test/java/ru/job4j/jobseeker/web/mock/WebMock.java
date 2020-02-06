package ru.job4j.jobseeker.web.mock;

import org.mockito.Mockito;
import org.mockito.internal.util.io.IOUtil;
import ru.job4j.jobseeker.model.User;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static ru.job4j.jobseeker.TestHelper.USER_SENSITIVE_MATCHERS;
import static ru.job4j.jobseeker.web.mock.WebMockUtil.*;

public class WebMock {
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";

    private static final String CONTEXT_PATH = "/app";

    private static final FilterConfig mockConfig = mock(FilterConfig.class);
    private static final FilterChain mockChain = mock(FilterChain.class);

    @Inject
    private Filter dispatcher;

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    private AuthManager authHolder;

    private StringBuilder forwardPathHolder;
    private StringBuilder redirectPathHolder;

    private AtomicInteger responseStatusHolder;
    private StringBuilder responseErrorMessageHolder;

    private Writer responseCharsHolder;

    public WebMock setUp() throws ServletException {
        dispatcher.init(mockConfig);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        return this;
    }

    public WebMock withNoDefaultStatusExpected() {
        responseStatusHolder = addResponseStatusListener(response);
        return this;
    }

    public WebMock withUrl(String url) {
        mockRequestURI(request, CONTEXT_PATH, url);
        return this;
    }

    public WebMock withNoAuth() {
        mockParameter(request, "action", ""); // to pass thru unauthorizedFilter w/o mockito warnings
        return withAuth(null);
    }

    public WebMock withAuth(User user) {
        authHolder = mockAuthorization(request, user);
        return this;
    }

    public WebMock withMethod(String method) {
        mockMethod(request, method);
        return this;
    }

    public WebMock withFormType(String data) {
        mockParameter(request, "form", data);
        return this;
    }

    public WebMock withAction(String action) {
        mockParameter(request, "action", action);
        return this;
    }

    public <V> WebMock withRequestParameter(String key, V value) {
        mockParameter(request, key, String.valueOf(value));
        return this;
    }

    public WebMock withRequestJson(String json) throws IOException {
        mockRequestReader(request, json);
        return this;
    }

    public WebMock withAnswerExpected() throws IOException {
        responseCharsHolder = addResponseBodyListener(response);
        return this;
    }

    public WebMock withForwardExpected() {
        forwardPathHolder = addRequestForwardListener(request);
        return this;
    }

    public WebMock withForwardWithErrorExpected() throws IOException {
        responseErrorMessageHolder = addRequestForwardErrorListener(response);
        return this;
    }

    public WebMock withRedirectExpected() throws IOException {
        redirectPathHolder = addRequestRedirectListener(response);
        return this;
    }

    public WebMock withRequestException(String parameter, Throwable e) {
        mockRolledExceptionThrown(request, e, parameter);
        return this;
    }

    public WebMock act() {
        try {
            dispatcher.doFilter(request, response, mockChain);
            return this;
        } catch (Exception e) {
            throw new IllegalStateException("Something going wrong, try to review the mock settings", e);
        }
    }

    public WebMock checkResponseStatus(int code) {
        if (code == HttpServletResponse.SC_OK) {
            Mockito.verify(response, never()).setStatus(anyInt()); // by default
        } else {
            Mockito.verify(response).setStatus(anyInt());
            assertEquals(code, responseStatusHolder.get());
        }
        return this;
    }

    public WebMock checkAuthorized(User expected) {
        assertNotNull(authHolder, "The changing auth in due the requests processing has not been set as expected");
        var actual = authHolder.getAuth();
        USER_SENSITIVE_MATCHERS.assertMatch(actual, expected);
        return this;
    }

    public WebMock checkUnauthorized() {
        assertNotNull(authHolder, "The changing auth in due the requests processing has not been set as expected");
        assertNull(authHolder.getAuth());
        return this;
    }

    public WebMock checkResponseBody(String expectedResponseBody) {
        assertNotNull(responseCharsHolder, "The answer has not been set as expected");
        assertEquals(expectedResponseBody, responseCharsHolder.toString());
        return this;
    }

    public String getResponseBody() {
        return responseCharsHolder.toString();
    }

    public WebMock checkForwardPath(String expectedPath) {
        assertNotNull(forwardPathHolder, "The forward has not been set as expected");
        assertEquals(expectedPath, forwardPathHolder.toString());
        return this;
    }

    public WebMock checkRedirectPath(String expectedPath) {
        assertNotNull(redirectPathHolder, "The redirection has not been set as expected");
        assertTrue(redirectPathHolder.toString().endsWith(expectedPath));
        return this;
    }

    public WebMock checkForwardErrorMessage(String expected) {
        assertNotNull(responseErrorMessageHolder, "The forward with error has not been set as expected");
        assertEquals(expected, responseErrorMessageHolder.toString());
        return this;
    }

    public void terminate() {
        IOUtil.closeQuietly(responseCharsHolder);
        dispatcher.destroy();
    }
}
