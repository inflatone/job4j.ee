package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "car")
@NamedEntityGraphs({@NamedEntityGraph(name = Car.WITH_DETAILS, attributeNodes = {@NamedAttributeNode("body"), @NamedAttributeNode("engine"), @NamedAttributeNode("transmission"), @NamedAttributeNode("vendor")})})
@Getter
@Setter
@NoArgsConstructor
public class Car extends BaseEntity {
    public static final String WITH_DETAILS = "graph: car c join details";

    public static final int MIN_YEAR = 1950;

    public static final int MAX_YEAR = 2020;

    @NotNull(message = "must be chosen")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @NotEmpty
    @Column(name = "model", nullable = false)
    private String model;

    @NotNull(message = "must be set")
    @Range(min = MIN_YEAR, max = MAX_YEAR, message = "choose correct year (1950 — 2020)")
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull(message = "must be set")
    @Column(name = "mileage", nullable = false, columnDefinition = "integer default 0")
    private Integer mileage;

    @NotNull(message = "must be chosen")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_id", nullable = false)
    private Body body;

    @NotNull(message = "must be chosen")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", nullable = false)
    private Engine engine;

    @NotNull(message = "must be chosen")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transmission_id", nullable = false)
    private Transmission transmission;

    public Car(Integer id, Vendor vendor, String model, Integer year, Integer mileage, Body body, Engine engine, Transmission transmission) {
        super(id);
        this.vendor = vendor;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.body = body;
        this.engine = engine;
        this.transmission = transmission;
    }
}