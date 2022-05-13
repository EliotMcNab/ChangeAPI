package com.company.collections.changeAPI.information;

import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;

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

    static ChangeInformation<Object> sum() {
        return array -> {
            int sum = 0;
            for (Object o : array) {
                sum += o.hashCode();
            }
            return sum;
        };
    }
}
