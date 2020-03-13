package ru.job4j.auto.to;

import lombok.Getter;

import java.net.URI;

@Getter
public class ImageTo extends BaseTo {
    private final String name;

    private final boolean blank;

    public ImageTo(String name, boolean blank, URI url, URI urlToModify) {
        super(url, urlToModify);
        this.name = name;
        this.blank = blank;
    }
}
