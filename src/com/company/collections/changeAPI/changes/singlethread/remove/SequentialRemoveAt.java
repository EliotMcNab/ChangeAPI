package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;

import java.util.Arrays;

/**
 * Sequential implementation of {@link RemoveAt} which groups all indexes to remove, ignores duplicates and removes them
 * all at once
 * @param <E> the type the {@link SingleThreadChange} operates on
 */
public class SequentialRemoveAt<E> extends RemoveBase<E> {

    // =====================================
    //               FIELDS
    // =====================================

    private final Change<E>[] changes;

    // ==================================
    //            CONSTRUCTOR
    // ==================================

    public SequentialRemoveAt(
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

    private int[] concatenateRemovalIndexes(final Change<E>[] changes) {
        int totalLength = 0;
        for (Change<E> change : changes) {
            totalLength += ((RemoveBase<E>) change).removalIndexes.length;
        }

        final int[] result = new int[totalLength];

        int k = 0;
        for (Change<E> change : changes) {
            final RemoveBase<E> remove = (RemoveBase<E>) change;
            System.arraycopy(remove.removalIndexes, 0, result, k, remove.removalIndexes.length);
            k += remove.removalIndexes.length;
        }

        return result;
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        final int[] allIndexes = concatenateRemovalIndexes(changes);
        final int[] indexes = ArrayUtil.retainDistinct(allIndexes);
        return ArrayUtil.removeAt(array, indexes);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "SequentialRemoveAt{" +
                Arrays.toString(changes) +
                "}";
    }
}
