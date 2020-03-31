package ru.job4j.auto.to;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseTo {
    private URI url;

    private URI urlToModify;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTo)) return false;
        BaseTo baseTo = (BaseTo) o;
        return Objects.equals(url, baseTo.url) &&
                Objects.equals(urlToModify, baseTo.urlToModify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, urlToModify);
    }
}
