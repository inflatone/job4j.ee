package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "vendor", uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "vendor_unique_name_idx")})
@BatchSize(size = 10)
@Getter
@Setter
@NoArgsConstructor
public class Vendor extends BaseEntity {
    private String name;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "logo_link")
    private String logoLink;

    public Vendor(Integer id, String name, String country, String logoLink) {
        super(id);
        this.name = name;
        this.country = country;
        this.logoLink = logoLink;
    }
}
