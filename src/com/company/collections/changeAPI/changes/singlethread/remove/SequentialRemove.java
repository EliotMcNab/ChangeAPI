package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ObjectComparator;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Sequential implementation of {@link RemoveAll} and {@link RemoveFirst} using a modified version of quickFind to search
 * simultaneously for all occurrences of certain values an only the first occurrences of others. Optimises this further
 * by ignoring duplicate values which need to have all instances found but also only the first instance (only looks for
 * all instances in that case)
 * @param <E> the type the {@link SingleThreadChange} operates on
 */
public class SequentialRemove<E> extends RemoveBase<E>{

    // =====================================
    //               FIELDS
    // =====================================

    private final Change<E>[] changes;

    // ==================================
    //            CONSTRUCTOR
    // ==================================

    public SequentialRemove(
            final Class<E> clazz,
            final Change<E>[] changes
    ) {
        super(
                clazz,
                null,
                null,
                null
        );
        this.changes = changes;
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return false;
    }

    private Change<E>[][] separateChanges(final Change<E>[] changes) {

        final Change<E>[][] result = new Change[4][];
        final Change<E>[] removeAll = new Change[changes.length];
        final Change<E>[] removeFirst = new Change[changes.length];

        int kAll = 0, kFirst = 0;

        for (Change<E> change : changes) {
            switch (change) {
                case RemoveAll   e -> removeAll[kAll++] = change;
                case RemoveFirst e -> removeFirst[kFirst++] = change;
                default            -> throw new IllegalArgumentException("Unhandled change class " + change);
            }
        }

        result[0] = Arrays.copyOf(removeAll, kAll);
        result[1] = Arrays.copyOf(removeFirst, kFirst);

        return result;
    }

    private Object[] concatenateToRemove(final Change<E>[] changes) {
        int length = 0;
        for (Object o : changes) {
            length += ((RemoveBase<E>) o).toRemove.length;
        }

        final Object[] result = new Object[length];

        int k = 0;
        for (Change<E> change : changes) {
            if (change == null) continue;

            final RemoveBase<E> remove = (RemoveBase<E>) change;
            System.arraycopy(remove.toRemove, 0, result, k, remove.toRemove.length);
            k += remove.toRemove.length;
        }

        return result;
    }

    @Override
    protected E[] applyToImpl(E[] array) {

        if (array.length == 0) return array;

        final Change<E>[][] separate = separateChanges(changes);
        final Change<E>[] removeAll = separate[0];
        final Change<E>[] removeFirst = separate[1];

        final Comparator<Object> comparator = new ObjectComparator();

        final Object[] removeAllValues = ArrayUtil.retainDistinct(concatenateToRemove(removeAll));
        final Object[] removeFirstValues = ArrayUtil.retainDistinct(concatenateToRemove(removeFirst));

        Arrays.parallelSort(removeAllValues, comparator);
        Arrays.parallelSort(removeFirstValues, comparator);

        final int[] removeAllIndexes = new int[array.length];
        final int[] removeFirstIndexes = new int[removeFirstValues.length];
        Arrays.fill(removeFirstIndexes, -1);

        int k = 0;
        for (int i = 0; i < array.length; i++) {
            final int indexAll = Arrays.binarySearch(removeAllValues, array[i], comparator);
            if (indexAll >= 0) {
                removeAllIndexes[k++] = i;
                continue;
            }

            if (removeFirstValues.length == 0) continue;

            final int indexFirst = Arrays.binarySearch(removeFirstValues, array[i], comparator);
            if (indexFirst >= 0 && (removeFirstIndexes[indexFirst] == -1 || i < removeFirstIndexes[indexFirst])) {
                removeFirstIndexes[indexFirst] = i;
            }
        }

        return ArrayUtil.removeAt(array, ArrayUtil.concatenate(Arrays.copyOf(removeAllIndexes, k), removeFirstIndexes));
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "SequentialRemove{changes=" +
                Arrays.toString(changes) +
                "}";
    }
}
