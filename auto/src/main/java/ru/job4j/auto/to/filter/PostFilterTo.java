package ru.job4j.auto.to.filter;

import lombok.Getter;
import lombok.Setter;
import ru.job4j.auto.model.Car;

import java.time.Instant;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Getter
@Setter
public class PostFilterTo extends BaseFilterTo {
    private Integer user;

    private Boolean image;

    private Boolean actual;

    private Integer vendor;

    private Integer body;

    private Integer engine;

    private Integer transmission;

    private YearRange year;

    private PriceRange price;

    private DateRange posted;

    private transient Map<String, Object> equalValues;

    private transient Map<String, Boolean> booleanValues;

    private transient Map<String, Range<?>> rangeValues;

    @Override
    public Map<String, Object> getEqualValues() {
        if (equalValues == null) {
            equalValues = newHashMap();
            // to skip null elements
            equalValues.computeIfAbsent("user", k -> user);
            equalValues.computeIfAbsent("vendor", k -> vendor);
            equalValues.computeIfAbsent("body", k -> body);
            equalValues.computeIfAbsent("engine", k -> engine);
            equalValues.computeIfAbsent("transmission", k -> transmission);
        }
        return equalValues;
    }

    @Override
    public Map<String, Boolean> getBooleanValues() {
        if (booleanValues == null) {
            booleanValues = newHashMap();
            booleanValues.computeIfAbsent("image", k -> image);
            booleanValues.computeIfAbsent("actual", k -> actual);
        }
        return booleanValues;
    }

    @Override
    public Map<String, Range<?>> getRangeValues() {
        if (rangeValues == null) {
            rangeValues = newHashMap();
            rangeValues.computeIfAbsent("year", k -> year);
            rangeValues.computeIfAbsent("price", k -> price);
            rangeValues.computeIfAbsent("posted", k -> posted);
        }
        return rangeValues;
    }

    protected static class YearRange extends Range<Integer> {
        @Override
        Integer defaultMin() {
            return Car.MIN_YEAR;
        }

        @Override
        Integer defaultMax() {
            return Car.MAX_YEAR;
        }
    }

    protected static class PriceRange extends Range<Integer> {
        @Override
        Integer defaultMin() {
            return -1;
        }

        @Override
        Integer defaultMax() {
            return Integer.MAX_VALUE;
        }
    }

    protected static class DateRange extends Range<Instant> {
        @Override
        Instant defaultMin() {
            return Instant.MIN;
        }

        @Override
        Instant defaultMax() {
            return Instant.MAX;
        }
    }
}
