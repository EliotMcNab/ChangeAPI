package com.company.collections.changeAPI.changes.singlethread.information;

import com.company.collections.changeAPI.changes.operations.Divisible;
import com.company.collections.changeAPI.changes.operations.Multipliable;
import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.changes.operations.Summable;
import com.company.utilities.ArrayUtil;
import com.company.utilities.TypeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public interface ChangeInformation<T> {
    Object getInformation(T[] array);

    static ChangeInformation<Object> findFirst(final @NotNull Object... toFind) {
        return array -> ArrayUtil.quickFindFirst(array, toFind);
    }

    static ChangeInformation<Object> findAll(final @NotNull Object... toFind) {
        return array -> ArrayUtil.quickFindAll(array, toFind);
    }

    static ChangeInformation<Object> getAt(final int... indexes) {
        return array -> ArrayUtil.retainAt(array, indexes);
    }

    static <E> ChangeInformation<E> getFirst(final @NotNull Predicate<? super E> filter) {
        return array -> {
            final int firstMatch = ArrayUtil.findFirstMatch(array, filter);
            return firstMatch >= 0 ? array[firstMatch] : null;
        };
    }

    static <E> ChangeInformation<E> getAll(final @NotNull Predicate<? super E> filter) {
        return array -> ArrayUtil.retainAt(array, ArrayUtil.findAllMatches(array, filter));
    }

    static ChangeInformation<Object> count(final @NotNull Object... toFind) {
        return array -> ArrayUtil.countMatches(array, toFind);
    }

    static <E> ChangeInformation<E> count(final @NotNull Predicate<? super E> filter) {
        return array -> ArrayUtil.findAllMatches(array, filter).length;
    }

    static <E> ChangeInformation<E> sum() {
        return array -> {
            if (array.length == 0) return null;

            E value = array[0];

            if (array[0] instanceof Summable) {
                for (int i = 1; i < array.length; i++) {
                    value = ((Summable<E>)value).add(array[i]);
                }
            } else {
                final Operator<E> operator = (Operator<E>) TypeUtil.getBasicOperator(array.getClass().getComponentType());
                for (int i = 1; i < array.length; i++) {
                    value = operator.add(value, array[i]);
                }
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> sum(
            @NotNull final Operator<E> operator
    ) {
        Objects.requireNonNull(operator);

        return array -> {
            if (array.length == 0) return 0;

            E value = array[0];
            for (int i = 1; i < array.length; i++) {
                value = operator.add(value, array[i]);
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> difference() {
        return array -> {
            if (array.length == 0) return null;

            E value = array[0];

            if (array[0] instanceof Summable) {
                for (int i = 1; i < array.length; i++) {
                    value = ((Summable<E>) value).sub(array[i]);
                }
            } else {
                final Operator<E> operator = (Operator<E>) TypeUtil.getBasicOperator(array.getClass().getComponentType());
                for (int i = 1; i < array.length; i++) {
                    value = operator.sub(value, array[i]);
                }
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> difference(
            @NotNull final Operator<E> operator
    ) {
        Objects.requireNonNull(operator);

        return array -> {
            if (array.length == 0) return 0;

            E value = array[0];
            for (int i = 1; i < array.length; i++) {
                value = operator.sub(value, array[i]);
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> multiply() {
        return array -> {
            if (array.length == 0) return null;

            E value = array[0];

            if (array[0] instanceof Multipliable) {
                for (int i = 1; i < array.length; i++) {
                    value = ((Multipliable<E>) value).mult(array[i]);
                }
            } else {
                final Operator<E> operator = (Operator<E>) TypeUtil.getBasicOperator(array.getClass().getComponentType());
                for (int i = 1; i < array.length; i++) {
                    value = operator.mult(value, array[i]);
                }
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> multiply(
            @NotNull final Operator<E> operator
    ) {
        Objects.requireNonNull(operator);

        return array -> {
            if (array.length == 0) return 0;

            E value = array[0];
            for (int i = 1; i < array.length; i++) {
                value = operator.mult(value, array[i]);
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> divide() {
        return array -> {
            if (array.length == 0) return null;

            E value = array[0];

            if (array[0] instanceof Divisible) {
                for (int i = 1; i < array.length; i++) {
                    value = ((Divisible<E>) value).div(array[i]);
                }
            } else {
                final Operator<E> operator = (Operator<E>) TypeUtil.getBasicOperator(array.getClass().getComponentType());
                for (int i = 1; i < array.length; i++) {
                    value = operator.div(value, array[i]);
                }
            }

            return value;
        };
    }

    static <E> ChangeInformation<E> divide(
            @NotNull final Operator<E> operator
    ) {
        Objects.requireNonNull(operator);

        return array -> {
            if (array.length == 0) return 0;

            E value = array[0];
            for (int i = 1; i < array.length; i++) {
                value = operator.div(value, array[i]);
            }

            return value;
        };
    }
}
