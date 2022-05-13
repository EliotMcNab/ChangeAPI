package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ObjectComparator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * {@link SingleThreadChange} responsible for removing all instance of every given element from an array. Allows the removal of
 * multiple elements at once.
 * @param <E> the type the SingleThreadChange operates on
 */
public class RemoveAll<E> extends RemoveBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
            RemoveAll.class,
            RemoveFirst.class
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RemoveAll(
            final Class<E> clazz,
            final Object[] toRemove
    ) {
        super(
                clazz,
                toRemove,
                null,
                new int[0]
        );
    }

    public RemoveAll(
            final Class<E> clazz,
            final Object[] toRemove,
            final Change<E> parent
            ) {
        super(
                clazz,
                toRemove,
                null,
                new int[0],
                parent
        );
    }

    public RemoveAll(
            final Class<E> clazz,
            final Collection<?> collection
    ) {
        super(
                clazz,
                collection.toArray(),
                null,
                new int[0]
        );
    }

    public RemoveAll(
            final Class<E> clazz,
            final Collection<?> collection,
            final Change<E> parent
    ) {
        super(
                clazz,
                collection.toArray(),
                null,
                new int[0],
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
    protected E[] applyToImpl(@NotNull E[] array) {
        Objects.requireNonNull(array);

        // used for sorting and searching gor elements
        final Comparator<Object> comparator = new ObjectComparator();

        // keeps only the unique elements to remove and sorts them
        final Object[] uniqueToRemove = ArrayUtil.retainDistinctImpl(toRemove, new ObjectComparator());
        Arrays.parallelSort(uniqueToRemove, comparator);

        // initialises the result array
        final E[] result = (E[]) Array.newInstance(clazz, array.length);

        final int stepSize = array.length / 16;

        // for every value in the given array...
        int k = 0;
        for (E value : array) {
            // ...if the value is not found...
            if (ArrayUtil.stepSearch(uniqueToRemove, value, comparator, stepSize) < 0) {
                // ...adds that value to the resulting array
                result[k++] = value;
            }
        }

        // returns the final resulting array
        return Arrays.copyOf(result, k);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "RemoveAll{toRemove=" +
                Arrays.toString(toRemove) +
                "}";
    }
}
