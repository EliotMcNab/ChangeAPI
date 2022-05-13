package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;

import java.util.Arrays;

/**
 * {@link SingleThreadChange} responsible for removing all elements at the given indexes from an array, ignoring invalid indexes.
 * Allows the removal of multiple indexes at once.
 * @param <E> the type the SingleThreadChange operates on
 */
public class RemoveAt<E> extends RemoveBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
            RemoveAt.class
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RemoveAt(
            final Class<E> clazz,
            final int[] indexes
    ) {
        super(
                clazz,
                null,
                null,
                indexes
        );
    }

    public RemoveAt(
            final Class<E> clazz,
            final int[] indexes,
            final Change<E> parent
    ) {
        super(
                clazz,
                null,
                null,
                indexes,
                parent
        );
        Arrays.parallelSort(indexes);
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return Arrays.asList(SEQUENTIALISEABLE).contains(change.getClass());
    }

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return new SequentialRemoveAt<>(clazz, changes);
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        return ArrayUtil.removeAt(array, removalIndexes);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "RemoveAt{" +
                Arrays.toString(removalIndexes) +
                "}";
    }
}
