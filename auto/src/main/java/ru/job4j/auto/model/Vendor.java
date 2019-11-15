package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Vendor extends BaseEntity {
    private String name;

    private String country;

    private String logoLink;

    public Vendor(Integer id, String name, String country, String logoLink) {
        super(id);
        this.name = name;
        this.country = country;
        this.logoLink = logoLink;
    }
}
