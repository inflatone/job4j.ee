package ru.job4j.auto.web.post;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.job4j.auto.to.filter.BaseFilterTo;

@Component
public class FilterToValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return BaseFilterTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var filter = (BaseFilterTo) target;
        filter.getRangeValues().forEach((k, v) -> {
            if (v != null && v.range() < 0) {
                errors.rejectValue(k, "", "Range is incorrect");
            }
        });
    }
}
