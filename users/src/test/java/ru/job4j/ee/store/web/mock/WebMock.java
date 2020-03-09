package ru.job4j.ee.store.web.mock;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.mockito.Mockito;
import org.mockito.internal.util.io.IOUtil;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.model.UserImage;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static ru.job4j.ee.store.AssertionUtil.assertMatch;
import static ru.job4j.ee.store.web.mock.WebMockUtil.*;

public class WebMock {
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";

    private static final String CONTEXT_PATH = "/app";

    private static final FilterConfig mockConfig = mock(FilterConfig.class);
    private static final FilterChain mockChain = mock(FilterChain.class);

    private Filter dispatcher;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private AtomicReference<User> authHolder;

    private StringBuilder forwardPathHolder;
    private StringBuilder redirectPathHolder;

    private AtomicInteger responseStatusHolder;
    private StringBuilder responseErrorMessageHolder;

    private Writer responseCharsHolder;
    private ByteArrayOutputStream responseBytesHolder;
    private Map<String, String> responseHeadersHolder;

    public static WebMock create() {
        return new WebMock();
    }

    private WebMock() {
    }

    public WebMock setUp(Injector injector) throws ServletException {
        this.dispatcher = injector.getInstance(GuiceFilter.class);
        dispatcher.init(mockConfig);

        injector.injectMembers(this);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseStatusHolder = addResponseStatusListener(response);
        return this;
    }

    public WebMock withUrl(String url) {
        mockRequestURI(request, CONTEXT_PATH, url);
        return this;
    }

    public WebMock withNoAuth() {
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

    public WebMock withDataType(String data) {
        mockParameter(request, "data", data);
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

    public WebMock withRequestImage(UserImage image) throws IOException {
        mockRequestMultipart(request, image, "image");
        return this;
    }

    public WebMock withRequestImage(UserImage image, String imageFieldName) throws IOException {
        mockRequestMultipart(request, image, imageFieldName);
        return this;
    }

    public WebMock withAnswerExpected() throws IOException {
        responseCharsHolder = addResponseBodyListener(response);
        return this;
    }

    public WebMock withBinaryAnswerExpected() throws IOException {
        responseBytesHolder = addResponseBinaryBodyListener(response);
        responseHeadersHolder = addResponseHeadersListener(response);
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
        mockRolledExceptionThrown(request, new IOException(e), parameter);
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
        var actual = authHolder.get();
        assertMatch(actual, expected);
        return this;
    }

    public WebMock checkUnauthorized() {
        assertNotNull(authHolder, "The changing auth in due the requests processing has not been set as expected");
        assertNull(authHolder.get());
        return this;
    }

    public WebMock checkResponseBody(String expectedResponseBody) {
        assertNotNull(responseCharsHolder, "The answer has not been set as expected");
        assertEquals(expectedResponseBody, responseCharsHolder.toString());
        return this;
    }

    public WebMock checkResponseBinaryBody(byte[] expected) {
        assertNotNull(responseBytesHolder, "The binary answer has not been set as expected");
        assertArrayEquals(expected, responseBytesHolder.toByteArray());
        return this;
    }

    public <V> WebMock checkResponseHeader(String key, V value) {
        var headerValue = responseHeadersHolder.get(key);
        assertNotNull(headerValue);
        assertTrue(headerValue.contains(String.valueOf(value)));
        return this;
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
        IOUtil.closeQuietly(responseBytesHolder);
        dispatcher.destroy();
    }
}
