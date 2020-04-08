package ru.job4j.auto.to;

import lombok.Getter;

import java.net.URI;
import java.util.Map;

@Getter
public class ErrorInfo extends BaseTo {
    private final String message;
    private final Map<String, String> details;

    public ErrorInfo(CharSequence url, String message, Map<String, String> details) {
        super(URI.create(url.toString()), null);
        this.message = message;
        this.details = details;
    }
}