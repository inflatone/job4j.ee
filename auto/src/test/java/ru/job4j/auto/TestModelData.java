package ru.job4j.auto;

import ru.job4j.auto.model.*;

import java.util.List;

public class TestModelData {
    public static Body SEDAN = new Body(30, "Sedan");
    public static Body COUPE = new Body(31, "Coupe");

    public static Engine DIESEL = new Engine(20, "Diesel");
    public static Engine ELECTRIC = new Engine(21, "Electric");

    public static Transmission AUTOMATIC = new Transmission(10, "automatic");
    public static Transmission MANUAL = new Transmission(11, "manual");

    public static Vendor MAZDA = new Vendor(50, "Mazda", "Japan", null);
    public static Vendor BMW = new Vendor(51, "BMW", "Germany", null);

    public static Car CAR_MAZDA3 = new Car(102, MAZDA, "MAZDA3", 2018, 0, SEDAN, ELECTRIC, AUTOMATIC);
    public static Car CAR_MAZDA6 = new Car(100, MAZDA, "MAZDA6", 2015, 0, SEDAN, DIESEL, AUTOMATIC);
    public static Car CAR_BMW = new Car(101, BMW, "M5 RWD", 2018, 150, COUPE, ELECTRIC, MANUAL);

    public static User USER = new User(1, "User", "user", "password", Role.USER);
    public static User DEALER = new User(2, "Admin", "dealer", "dealer", Role.ADMIN);

    public static Post POST_MAZDA6 = new Post(150, "cell car asap", "mazda 6 description", null, 300000, CAR_MAZDA6, USER);
    public static Post POST_BMW = new Post(151, "nice car", "bmw m5 description", null, null, CAR_BMW, USER);
    public static Post POST_MAZDA3 = new Post(152, "beauty babe", "mazda 3 description", null, 550000, CAR_MAZDA3, DEALER);

    public static List<Body> BODIES = List.of(SEDAN, COUPE);
    public static List<Engine> ENGINES = List.of(DIESEL, ELECTRIC);
    public static List<Transmission> TRANSMISSIONS = List.of(AUTOMATIC, MANUAL);
    public static List<Vendor> VENDORS = List.of(MAZDA, BMW);

    public static List<Car> CARS = List.of(CAR_MAZDA6, CAR_BMW, CAR_MAZDA3);
    public static List<User> USERS = List.of(DEALER, USER);
    public static List<Post> POSTS = List.of(POST_MAZDA6, POST_BMW, POST_MAZDA3);
}
