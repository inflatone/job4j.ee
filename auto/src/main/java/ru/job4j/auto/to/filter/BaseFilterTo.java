package ru.job4j.auto.to.filter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

public abstract class BaseFilterTo {
    public abstract Map<String, Object> getEqualValues();

    public abstract Map<String, Boolean> getBooleanValues();

    public abstract Map<String, Range<?>> getRangeValues();

    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static abstract class Range<E extends Comparable<? super E>> {
        protected E min;

        protected E max;

        public E getMin() {
            return min != null ? min : defaultMin();
        }

        public E getMax() {
            return max != null ? max : defaultMax();
        }

        abstract E defaultMin();

        abstract E defaultMax();

        public int range() {
            return getMax().compareTo(getMin());
        }
    }
}
