package ru.job4j.auto.config;

import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import ru.job4j.auto.config.helper.*;
import ru.job4j.auto.config.helper.BaseDetailsTestHelperFacade.CarDetailTestHelper;
import ru.job4j.auto.config.helper.BaseEntityTestHelper.RoleEntityTestHelper;
import ru.job4j.auto.config.helper.EntityTestHelpers.CarEntityTestHelper;
import ru.job4j.auto.config.helper.EntityTestHelpers.UserEntityTestHelper;
import ru.job4j.auto.config.helper.ToTestHelpers.ImageToTestHelper;
import ru.job4j.auto.model.*;
import ru.job4j.auto.to.ImageTo;
import ru.job4j.auto.to.PostTo;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.converter.JsonHelper;
import ru.job4j.auto.web.converter.ModelConverter;

import java.io.IOException;

import static com.google.common.base.Throwables.getRootCause;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.auto.service.ImageService.NO_IMAGE_RESOURCE;

@Configuration
public class TestHelpersConfig {
    @Autowired(required = false)
    private JsonHelper jsonHelper;

    @Autowired(required = false)
    private ModelConverter modelConverter;

    @Bean
    public BaseDetailsTestHelperFacade carDetailsTestHelpersFacade() {
        return new BaseDetailsTestHelperFacade(jsonHelper);
    }

    @Bean
    public CarDetailTestHelper<Body> bodyEntityTestHelper() {
        return carDetailsTestHelpersFacade().getBodyEntityTestHelper();
    }

    @Bean
    public CarDetailTestHelper<Engine> engineEntityTestHelper() {
        return carDetailsTestHelpersFacade().getEngineEntityTestHelper();
    }

    @Bean
    public CarDetailTestHelper<Transmission> transmissionEntityTestHelper() {
        return carDetailsTestHelpersFacade().getTransmissionEntityTestHelper();
    }

    @Bean
    public CarDetailTestHelper<Vendor> vendorEntityTestHelper() {
        return carDetailsTestHelpersFacade().getVendorEntityTestHelper();
    }

    @Bean
    public BaseEntityTestHelper<Car> carEntityTestHelper() {
        return new CarEntityTestHelper(jsonHelper, carDetailsTestHelpersFacade());
    }

    @Bean
    public BaseEntityTestHelper<Post> postEntityTestHelper() {
        return new EntityTestHelpers.PostEntityTestHelper(jsonHelper, userEntityTestHelper(), carEntityTestHelper());
    }

    @Bean
    public BaseEntityTestHelper<User> userEntityTestHelper() {
        return new UserEntityTestHelper(jsonHelper);
    }

    @Bean
    public ImageEntityTestHelper imageEntityTestHelper(@Autowired Image defaultImage) {
        return new ImageEntityTestHelper(jsonHelper, defaultImage);
    }

    @Bean("defaultImage")
    public Image getDefaultImage() throws IOException {
        return new Image(null, "noImage.jpg", MediaType.IMAGE_JPEG_VALUE,
                Resources.toByteArray(Resources.getResource(NO_IMAGE_RESOURCE)));
    }

    @Bean // only for entity/json matching
    public RoleEntityTestHelper roleEntityTestHelper() {
        return new RoleEntityTestHelper(jsonHelper);
    }

    @Bean
    BaseToTestHelper<UserTo, User> userToTestHelper() {
        return new ToTestHelpers.UserToTestHelper(jsonHelper, modelConverter);
    }

    @Bean
    BaseToTestHelper<ImageTo, Image> imageToTestHelper() {
        return new ImageToTestHelper(jsonHelper, modelConverter);
    }

    @Bean
    public BaseToTestHelper<PostTo, Post> postToTestHelper() {
        return new ToTestHelpers.PostToTestHelper(jsonHelper, modelConverter);
    }

    // Check root cause in JUnit: https://github.com/junit-team/junit4/pull/778
    public static <T extends Throwable> void validateRootCause(Class<T> expectedClass, Runnable runnable) {
        assertThrows(expectedClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }
}
