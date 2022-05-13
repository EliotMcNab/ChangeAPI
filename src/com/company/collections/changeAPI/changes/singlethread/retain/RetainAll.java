package com.company.collections.changeAPI.changes.singlethread.retain;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ObjectComparator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * {@link SingleThreadChange} responsible for retaining all instances of given elements in an array
 * @param <E> the type the SingleThreadChange operates on
 */
public class RetainAll<E> extends RetainBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
            RetainAll.class,
            RetainFirst.class
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RetainAll(
            final Class<E> clazz,
            final Object[] toRetain
    ) {
        super(
                clazz,
                toRetain,
                null
        );
    }

    public RetainAll(
            final Class<E> clazz,
            final Object[] toRetain,
            final Change<E> parent
    ) {
        super(
                clazz,
                toRetain,
                null,
                parent
        );
    }

    public RetainAll(
            final Class<E> clazz,
            final Collection<?> c
    ) {
        super(
                clazz,
                c.toArray(),
                null
        );
    }

    public RetainAll(
            final Class<E> clazz,
            final Collection<?> c,
            final Change<E> parent
    ) {
        super(
                clazz,
                c.toArray(),
                null,
                parent
        );
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return Arrays.asList(SEQUENTIALISEABLE).contains(change.getClass());
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(clazz);

        final Comparator<Object> comparator = new ObjectComparator();

        final Object[] uniqueToRetain = ArrayUtil.retainDistinctImpl(toRetain, comparator);
        Arrays.parallelSort(uniqueToRetain, comparator);
        final E[] result = (E[]) Array.newInstance(clazz, array.length);

        int k = 0;
        for (int i = 0; i < array.length; i++) {
            final int index = Arrays.binarySearch(uniqueToRetain, array[i], comparator);
            if (index >= 0) result[k++] = array[i];
        }

        return Arrays.copyOf(result, k);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "RetainAll{toRetain=" +
                Arrays.toString(toRetain) +
                "}";
    }
}
