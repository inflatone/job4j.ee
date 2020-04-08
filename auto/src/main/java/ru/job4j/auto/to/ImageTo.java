package ru.job4j.auto.to;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URI;

@Getter
@NoArgsConstructor
public class ImageTo extends BaseTo {
    private String name;

    private boolean blank;

    public ImageTo(String name, boolean blank, URI url, URI urlToModify) {
        super(url, urlToModify);
        this.name = name;
        this.blank = blank;
    }
}
