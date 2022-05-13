package com.company.collections.changeAPI.changes.singlethread;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.parallel.ParallelOrigin;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * {@link SingleThreadChange} used as a base for further modifications. Only stores an initial array and does not result in any
 * change when applied to an array through toArray or applyTo
 * @param <E> the type the SingleThreadChange operates on
 */
public class Origin<E> extends SingleThreadChange<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public Origin(
            @NotNull final Class<E> clazz
    ) {
        super(
                clazz,
                null,
                (E[]) new Object[0]
        );
    }

    public Origin(
            @NotNull final Class<E> clazz,
            @NotNull final E[] array
    ) {
        super(
                clazz,
                null,
                array
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    public ParallelOrigin<E> parallel() {
        return new ParallelOrigin<>(clazz, array);
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
        return array;
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public E[] getArray() {
        return Arrays.copyOf(array, array.length);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "Origin{array=" +
                Arrays.toString(array) +
                "}";
    }
}
