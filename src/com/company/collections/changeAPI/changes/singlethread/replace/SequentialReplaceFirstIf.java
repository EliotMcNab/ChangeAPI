package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;

import java.util.Arrays;

/**
 * Sequential implementation of {@link ReplaceFirstIf} where {@link java.util.function.Predicate Predicate} are applied
 * consecutively to avoid looping through an array multiple times
 * @param <E> the type the {@link SingleThreadChange} operates on
 */
public class SequentialReplaceFirstIf<E> extends ReplaceBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private final Change<E>[] changes;

    // ====================================
    //             CONSTRUCTOR
    // ========w============================

    public SequentialReplaceFirstIf(
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

        final boolean[] matches = new boolean[changes.length]; // keeps track of which changes have been applied
        int matchCount = 0;                           // keeps track of how many changes have been applied

        int i = -1;
        // iterates through the array while there are still changes to apply
        while (++i < result.length && matchCount != changes.length) {
            // iterates through every change
            for (int j = 0; j < changes.length; j++) {
                // if the change has already been applied, moves on to the next change
                if (matches[j]) continue;

                // checks if the current element should be replaced by the change
                final ReplaceBase<E> replace = (ReplaceBase<E>) changes[j];
                if (replace.getFilter().test(result[i])) {
                    // replaces the element
                    result[i] = (E) replace.values[0];
                    // marks the changes as having occurred
                    matches[j] = true;
                    // counts the change, so we are able to stop loop when all changes have been found
                    matchCount++;
                }
            }
        }

        // returns the final result
        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "SequentialReplaceFirstIf{changes=" +
                Arrays.toString(changes) +
                "}";
    }
}
