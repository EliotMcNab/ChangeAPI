package com.company.collections.changeAPI.changes.singlethread.functions;

import com.company.utilities.comparators.ObjectComparator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public abstract class Functions {

    public static <T> Function<T[], T[]> forEach(
            @NotNull final Function<T, T> function
    ) {
        return array -> {
            final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);

            for (int i = 0; i < result.length; i++) {
                result[i] = function.apply(array[i]);
            }

            return result;
        };
    }

    public static <T> Function<T[], T[]> sort() {
        return sort((Comparator<T>) new ObjectComparator());
    }

    public static <T> Function<T[], T[]> sort(
            @NotNull final Comparator<T> comparator
    ) {
        return array -> {
            final T[] result = Arrays.copyOf(array, array.length);
            Arrays.parallelSort(result, comparator);
            return result;
        };
    }

    public static <T> Function<T[], T[]> clear() {
        return array -> (T[]) Array.newInstance(array.getClass().getComponentType(), 0);
    }
}
