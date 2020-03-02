package ru.job4j.auto.to;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorInfo {
    private final String url;
    private final String message;
    private final Map<String, String> details;

    public ErrorInfo(CharSequence url, String message) {
        this(url, message, null);
    }

    public ErrorInfo(CharSequence url, String message, Map<String, String> details) {
        this.url = url.toString();
        this.message = message;
        this.details = details;
    }
}