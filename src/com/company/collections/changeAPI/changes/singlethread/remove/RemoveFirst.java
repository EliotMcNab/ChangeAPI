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

/**
 * {@link SingleThreadChange} responsible for removing the first element of every given element from an array. Can remove multiple
 * elements at once.
 * @param <E> the type the SingleThreadChange operates on
 */
public class RemoveFirst<E> extends RemoveBase<E> {

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

    public RemoveFirst(
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

    public RemoveFirst(
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

    public RemoveFirst(
            final Class<E> clazz,
            final Collection<? extends E> c
    ) {
        super(
                clazz,
                c.toArray(),
                null,
                new int[0]
        );
    }

    public RemoveFirst(
            final Class<E> clazz,
            final Collection<? extends E> c,
            final Change<E> parent
    ) {
        super(
                clazz,
                c.toArray(),
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
        // used to compare and sort objects
        final Comparator<Object> comparator = new ObjectComparator();

        // keeps only the unique value to removes and sorts them
        final Object[] uniqueToRemove = ArrayUtil.retainDistinct(toRemove);
        Arrays.parallelSort(uniqueToRemove, comparator);

        // initialises the result array
        final E[] result = (E[]) Array.newInstance(clazz, array.length);
        // keeps track of which values have already been found
        final boolean[] found = new boolean[uniqueToRemove.length];

        // iterates over every value in the array
        int k = 0;
        for (E value : array) {
            // searches for that value among the values to be found
            final int index = Arrays.binarySearch(uniqueToRemove, value);
            // if the value is found for the first time...
            if (index >= 0 && !found[index]) {
                // ...marks it as having been found and moves on to the next value
                found[index] = true;
                continue;
            }
            // if the value hasn't been found, or was already found, adds it to the result array
            result[k++] = value;
        }

        // returns the final array
        return Arrays.copyOf(result, k);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "RemoveFirst{toRemove=" +
                Arrays.toString(toRemove) +
                "}";
    }
}
