package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ArrayElementComparator;

import java.util.Arrays;
import java.util.Comparator;

/**
 * {@link SingleThreadChange} responsible for replacing the first or last instance of a given element in an array with a new value
 * @param <E> the type the SingleThreadChange operates on
 */
public class ReplaceFirstOrLast<E> extends ReplaceValues<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private final boolean replaceLast;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ReplaceFirstOrLast(
            final Class<E> clazz,
            final E[] toReplace
    ) {
        super(
                clazz,
                toReplace,
                null
        );
        this.replaceLast = false;
    }

    public ReplaceFirstOrLast(
            final Class<E> clazz,
            final E[] toReplace,
            final Change<E> parent
    ) {
        super(
                clazz,
                toReplace,
                parent
        );
        this.replaceLast = false;
    }

    public ReplaceFirstOrLast(
            final Class<E> clazz,
            final E[] toReplace,
            final boolean replaceLast
    ) {
        super(
                clazz,
                toReplace
        );
        this.replaceLast = replaceLast;
    }

    public ReplaceFirstOrLast(
            final Class<E> clazz,
            final E[] toReplace,
            final boolean replaceLast,
            final Change<E> parent
    ) {
        super(
                clazz,
                toReplace,
                parent
        );
        this.replaceLast = replaceLast;
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return false;
    }

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return null;
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        // comparator used for sorting wrapped arrays
        final Comparator<Object[]> comparator = new ArrayElementComparator<>(0);

        // maps the values to replace to their replacing values & sorts them
        final Object[][] wrapped = ArrayUtil.wrapArrays(getEvenIndexes(), getOddIndexes());
        Arrays.parallelSort(wrapped, comparator);

        final Object[][] indexMap = new Object[wrapped.length][2];
        Arrays.fill(indexMap, new Object[]{-1, null});

        // creates the result array
        final E[] result = Arrays.copyOf(array, array.length);

        for (int i = 0; i < result.length; i++) {
            final int index = Arrays.binarySearch(wrapped, new Object[]{result[i]}, comparator);
            if (index >= 0 && ((int) indexMap[index][0] == -1 || i < (int) indexMap[index][0]) != replaceLast) {
                indexMap[index] = new Object[]{i, wrapped[index][1]};
            }
        }

        for (int i = 0; i < indexMap.length; i++) {
            final int index = (int) indexMap[i][0];
            if (index >= 0) result[index] = (E) indexMap[i][1];
        }

        // returns the final result
        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ReplaceFirstOrLast{toReplace=" +
                Arrays.toString(getEvenIndexes()) +
                ", replacing=" +
                Arrays.toString(getOddIndexes()) +
                "}";
    }
}
