package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Car extends BaseEntity {
    private Vendor vendor;

    private String model;

    private Integer year;

    private Integer mileage;

    private Body body;

    private Engine engine;

    private Transmission transmission;

    private Post post;

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