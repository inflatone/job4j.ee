package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "car")
@NamedEntityGraphs({@NamedEntityGraph(name = Car.WITH_DETAILS, attributeNodes = {@NamedAttributeNode("body"), @NamedAttributeNode("engine"), @NamedAttributeNode("transmission"), @NamedAttributeNode("vendor")})})
@Getter
@Setter
@NoArgsConstructor
public class Car extends BaseEntity {
    public static final String WITH_DETAILS = "graph: car c join details";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "mileage", nullable = false, columnDefinition = "integer default 0")
    private Integer mileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_id", nullable = false)
    private Body body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", nullable = false)
    private Engine engine;

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