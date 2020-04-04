package ru.job4j.auto.web.converter;

import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Locale;

public class DateFormatter implements Formatter<Instant> {
    @Override
    public Instant parse(@Nullable String line, @Nullable Locale locale) {
        return StringUtils.isEmpty(line) ? null : Instant.ofEpochMilli(Long.parseLong(line));
    }

    @Override
    public String print(@Nullable Instant date, @Nullable Locale locale) {
        return date == null ? "" : String.valueOf(date.toEpochMilli());
    }
}
