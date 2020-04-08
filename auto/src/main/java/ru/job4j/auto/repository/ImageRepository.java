package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Image;

@Repository
public class ImageRepository extends BaseEntityRepository<Image> {
    public ImageRepository() {
        super(Image.class);
    }
}
