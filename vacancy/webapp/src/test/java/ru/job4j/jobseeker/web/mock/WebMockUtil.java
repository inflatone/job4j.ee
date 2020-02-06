package ru.job4j.jobseeker.web.mock;

import ru.job4j.jobseeker.model.User;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class WebMockUtil {
    private WebMockUtil() {
        throw new IllegalStateException("should not instantiated");
    }

    static AuthManager mockAuthorization(HttpServletRequest mockRequest, User authUser) {
        final var authManager = new AuthManager();
        authManager.setAuth(authUser, true);
        var mockSession = mock(HttpSession.class);

        when(mockSession.getAttribute("Key[type=ru.job4j.jobseeker.web.security.AuthManager, annotation=[none]]"))
                .thenReturn(authManager);

        when(mockRequest.getSession()).thenReturn(mockSession);
        return authManager;
    }

    static void mockRequestURI(HttpServletRequest mockRequest, final String contextPath, final String uri) {
        when(mockRequest.getContextPath()).thenReturn(contextPath);
        when(mockRequest.getRequestURI()).thenReturn(contextPath + uri);
    }

    static void mockMethod(HttpServletRequest mockRequest, String method) {
        lenient().when(mockRequest.getMethod()).thenReturn(method);
    }

    static void mockParameter(HttpServletRequest mockRequest, String key, String value) {
        when(mockRequest.getParameter(key)).thenReturn(value);
    }

    static void mockRequestReader(HttpServletRequest mockRequest, String chars) throws IOException {
        var mockReader = new BufferedReader(new StringReader(chars));
        when(mockRequest.getReader()).thenReturn(mockReader);
    }

    static void mockRolledExceptionThrown(HttpServletRequest mockRequest, Throwable e, String parameter) {
        doThrow(new RuntimeException(null, new Exception(new IOException(e)))).when(mockRequest).getParameter(parameter);
    }

    static AtomicInteger addResponseStatusListener(HttpServletResponse mockResponse) {
        final var buffer = new AtomicInteger();
        doAnswer(invocation -> {
            buffer.set(invocation.getArgument(0));
            return null;
        }).when(mockResponse).setStatus(anyInt());
        return buffer;
    }

    static Writer addResponseBodyListener(HttpServletResponse mockResponse) throws IOException {
        final var buffer = new CharArrayWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(buffer));
        return buffer;
    }

    static StringBuilder addRequestForwardListener(HttpServletRequest mockRequest) {
        final var buffer = new StringBuilder();
        var mockDispatcher = mock(RequestDispatcher.class);
        doAnswer(invocation -> {
            var actualUrl = (String) invocation.getArgument(0);
            buffer.append(actualUrl);
            return mockDispatcher;
        }).when(mockRequest).getRequestDispatcher(anyString());
        return buffer;
    }

    static StringBuilder addRequestForwardErrorListener(HttpServletResponse mockResponse) throws IOException {
        final var buffer = new StringBuilder();
        doAnswer(invocation -> {
            mockResponse.setStatus(invocation.getArgument(0));
            buffer.append((String) invocation.getArgument(1));
            return null;
        }).when(mockResponse).sendError(anyInt(), anyString());
        return buffer;
    }

    static StringBuilder addRequestRedirectListener(HttpServletResponse mockResponse) throws IOException {
        final var buffer = new StringBuilder();
        doAnswer(invocation -> {
            var path = (String) invocation.getArgument(0);
            buffer.append(path);
            return null;
        }).when(mockResponse).sendRedirect(anyString());
        return buffer;
    }
}