package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ArrayElementComparator;

import java.util.Arrays;
import java.util.Comparator;

/**
 * {@link SingleThreadChange} responsible for replacing all given elements in an array with new values. Can replace multiple elements
 * with different values associated to each of them at once.<br><br>
 *
 * This SingleThreadChange is not sequentialisable due to how each {@link ReplaceAll} modifies the values in an array and therefore
 * require looping through it again in case values which have just been modified need to be replaced
 * @param <E> the type the SingleThreadChange operates on
 */
public class ReplaceAll<E> extends ReplaceValues<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ReplaceAll(
            final Class<E> clazz,
            final E[] toReplace
    ) {
        super(
                clazz,
                toReplace,
                null
        );
    }

    public ReplaceAll(
            final Class<E> clazz,
            final E[] toReplace,
            final Change<E> parent
    ) {
        super(
                clazz,
                toReplace,
                parent
        );
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

        // creates the result array
        final E[] result = Arrays.copyOf(array, array.length);

        // for every value in the result array...
        for (int i = 0; i < result.length; i++) {
            // ...looks for the value in the values to replace...
            final int index = Arrays.binarySearch(wrapped, new Object[]{result[i]}, comparator);
            // ...and if the value is found, replaces it
            if (index >= 0) result[i] = (E) wrapped[index][1];
        }

        // returns the final result
        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ReplaceAll{toReplace=" +
                Arrays.toString(getEvenIndexes()) +
                ", replacing=" +
                Arrays.toString(getOddIndexes()) +
                "}";
    }
}
