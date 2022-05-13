package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Sequential implementation of {@link RemoveIf} which applies every {@link Predicate} consecutively on every value in
 * an array instead of looping multiple time throughout an array
 * @param <E> the type the {@link SingleThreadChange} operates on
 */
public class SequentialRemoveIf<E> extends RemoveBase<E> {

    // =====================================
    //               FIELDS
    // =====================================

    private final Change<E>[] changes;

    // ==================================
    //            CONSTRUCTOR
    // ==================================

    public SequentialRemoveIf(
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

    private Predicate<? super E>[] concatenateFilters(final Change<E>[] changes) {
        final Predicate<? super E>[] result = new Predicate[changes.length];

        for (int i = 0; i < changes.length; i++) {
            result[i] = ((RemoveBase<E>)changes[i]).getFilter();
        }

        return result;
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        final E[] result = (E[]) Array.newInstance(clazz, array.length);

        final Predicate<? super E>[] filters = concatenateFilters(changes);

        int k = 0;
        mainLoop: for (E e : array) {
            for (Predicate<? super E> predicate : filters) {
                if (predicate.test(e)) continue mainLoop;
            }
            result[k++] = e;
        }

        return Arrays.copyOf(result, k);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "SequentialRemoveIf{" +
                Arrays.toString(changes) +
                "}";
    }
}
