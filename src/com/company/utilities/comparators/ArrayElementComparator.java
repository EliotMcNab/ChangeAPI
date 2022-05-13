package com.company.utilities.comparators;

import java.util.Comparator;

/**
 * Used to compare two 1D arrays together where only the nth element is taken into consideration
 */
public class ArrayElementComparator<T> implements Comparator<T[]> {
    private final int index;
    private final Comparator<T> comparator;

    public ArrayElementComparator(
            final int index
    ) {
        this.index = index;
        this.comparator = (Comparator<T>) new ObjectComparator();
    }

    public ArrayElementComparator(
            final int index,
            final Comparator<T> comparator
    ) {
        this.index = index;
        this.comparator = comparator;
    }

    @Override
    public int compare(T[] o1, T[] o2) {
        return comparator.compare(o1[index], o2[index]);
    }
}
