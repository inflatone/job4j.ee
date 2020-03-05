package ru.job4j.auto;

import javax.validation.groups.Default;

public class View {
    // Validate only when DB save
    public interface Persist extends Default {
    }
}