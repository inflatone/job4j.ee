package ru.job4j.ee.store.web.mock;

import org.mockito.Mockito;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.model.UserImage;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.io.InputStream.nullInputStream;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class WebMockUtil {
    private WebMockUtil() {
        throw new IllegalStateException("should not instantiated");
    }

    static AtomicReference<User> mockAuthorization(HttpServletRequest mockRequest, User authUser) {
        final var authWrapper = new AtomicReference<>(authUser);
        var mockSession = mock(HttpSession.class);
        doAnswer(invocation -> {
            authWrapper.set(invocation.getArgument(1));
            return null;
        }).when(mockSession).setAttribute(anyString(), any());
        doAnswer(invocation -> {
            authWrapper.set(null);
            return null;
        }).when(mockSession).invalidate();

        when(mockSession.getAttribute("authUser")).thenReturn(authWrapper.get());

        when(mockRequest.getSession(anyBoolean())).thenReturn(mockSession);
        when(mockRequest.getSession()).thenReturn(mockSession);
        return authWrapper;
    }

    static void mockRequestURI(HttpServletRequest mockRequest, final String contextPath, final String uri) {
        when(mockRequest.getContextPath()).thenReturn(contextPath);
        when(mockRequest.getRequestURI()).thenReturn(contextPath + uri);
    }

    static void mockMethod(HttpServletRequest mockRequest, String method) {
        when(mockRequest.getMethod()).thenReturn(method);
    }

    static void mockParameter(HttpServletRequest mockRequest, String key, String value) {
        when(mockRequest.getParameter(key)).thenReturn(value);
    }

    static void mockRequestReader(HttpServletRequest mockRequest, String chars) throws IOException {
        var mockReader = new BufferedReader(new StringReader(chars));
        when(mockRequest.getReader()).thenReturn(mockReader);
    }

    static void mockRolledExceptionThrown(HttpServletRequest mockRequest, Throwable e, String parameter) {
        doThrow(new RuntimeException(new WriteAbortedException("Error: " + e.getCause().getClass().getSimpleName(),
                new IOException(e)))).when(mockRequest).getParameter(parameter);
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
        new CharArrayWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(buffer));
        return buffer;
    }

    static ByteArrayOutputStream addResponseBinaryBodyListener(HttpServletResponse mockResponse) throws IOException {
        final var buffer = new ByteArrayOutputStream();
        var servletOut = Mockito.spy(ServletOutputStream.class);
        doAnswer(invocation -> {
            buffer.write((int) invocation.getArgument(0));
            return null;
        }).when(servletOut).write(anyInt());
        when(mockResponse.getOutputStream()).thenReturn(servletOut);
        return buffer;
    }

    static Map<String, String> addResponseHeadersListener(HttpServletResponse mockResponse) {
        final var buffer = new HashMap<String, String>();
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            buffer.put(key, value);
            return null;
        }).when(mockResponse).setHeader(anyString(), anyString());
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

    static void mockRequestMultipart(HttpServletRequest mockRequest, UserImage image, String imageFieldName) throws IOException {
        var boundary = "--boundary";
        when(mockRequest.getContentType()).thenReturn("multipart/form-data; boundary=" + boundary);

        var mockStream = mockServletInputStream(image, boundary, imageFieldName);
        when(mockRequest.getInputStream()).thenReturn(mockStream);
    }

    private static ServletInputStream mockServletInputStream(final UserImage image, String boundary, String imageFieldName) throws IOException {
        var bufferForMock = image == null ? nullInputStream() : createMockInputStream(boundary, List.of(
                new RequestContent("stub", null, null, new ByteArrayInputStream("stub-to-be-skipped".getBytes())), // form-field to be skipped
                new RequestContent(imageFieldName, image.getName(),
                        image.getContentType(), image.getData())
        ));
        var mock = Mockito.spy(ServletInputStream.class);
        doAnswer(invocation -> bufferForMock.read()).when(mock).read();
        return mock;
    }

    private static final String LS = "\r\n";

    private static InputStream createMockInputStream(String boundary, List<RequestContent> contents) {
        InputStream result = nullInputStream();
        for (final var content : contents) {
            result = new SequenceInputStream(result, createMockInputStream(boundary, content));
        }
        final var suffix = "--" + boundary + "--" + LS;
        return new SequenceInputStream(result, new ByteArrayInputStream(suffix.getBytes()));
    }

    private static InputStream createMockInputStream(String boundary, RequestContent content) {
        String header = "--" + boundary + LS
                + (content.disposition == null ? "" : "Content-Disposition: " + content.disposition + LS)
                + (content.type == null ? "" : "Content-Type: " + content.type + LS)
                + (content.disposition == null && content.type == null ? "" : LS);
        final var preData = new ByteArrayInputStream(header.getBytes());
        final var postData = new ByteArrayInputStream(LS.getBytes());
        return new SequenceInputStream(new SequenceInputStream(preData, content.data), postData);
    }

    private static class RequestContent {
        private final String disposition;
        private final String type;
        private final InputStream data;

        public RequestContent(String fieldName, String fileName, String type, InputStream data) {
            this.disposition = "form-data;"
                    + (fieldName == null ? "" : " name=\"" + fieldName + "\";")
                    + (fileName == null ? "" : " filename=\"" + fileName + "\";");
            this.type = type;
            this.data = data;
        }
    }
}