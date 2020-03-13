package ru.job4j.auto.to;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@Getter
@RequiredArgsConstructor
public class BaseTo {
    private final URI url;

    private final URI urlToModify;
}
