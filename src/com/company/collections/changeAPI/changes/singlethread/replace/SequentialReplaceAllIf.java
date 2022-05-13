package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Sequential implementation of {@link ReplaceAllIf} which applies each {@link Predicate} successively to avoid having
 * to iterate through an array multiple times
 * @param <E> the type the {@link SingleThreadChange} operates on
 */
public class SequentialReplaceAllIf<E> extends ReplaceBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private final Change<E>[] changes;

    // ====================================
    //             CONSTRUCTOR
    // ========w============================

    public SequentialReplaceAllIf(
            final Class<E> clazz,
            final Change<E>[] changes
    ) {
        super(
                clazz,
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

    @Override
    protected E[] applyToImpl(E[] array) {
        final E[] result = Arrays.copyOf(array, array.length);

        // iterates through every element in the array
        mainLoop: for (int i = 0; i < array.length; i++) {
            // iterates through every sequential change
            for (Change<E> change : changes) {
                // gets the SingleThreadChange's filer and replacing value
                final Predicate<? super E> filter = ((ReplaceBase<E>) change).getFilter();
                final E replacingValue = (E) ((ReplaceBase<E>) change).values[0];

                // if the current array element matches the SingleThreadChange's filter...
                if (filter.test(array[i])) {
                    // ...replaces its values...
                    result[i] = replacingValue;
                    // ...and moves on to the next value in the array
                    continue mainLoop;
                }
            }
        }

        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "SequentialReplaceAllIf{changes=" +
                Arrays.toString(changes) +
                "}";
    }
}
