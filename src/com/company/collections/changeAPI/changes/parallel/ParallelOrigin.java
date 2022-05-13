package com.company.collections.changeAPI.changes.parallel;

import com.company.collections.changeAPI.Change;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ParallelOrigin<E> extends ParallelChange<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelOrigin(
            @NotNull final Class<E> clazz
    ) {
        super(clazz);
    }

    public ParallelOrigin(
            @NotNull final Class<E> clazz,
            @NotNull final E[] array
    ) {
        super(
                clazz,
                getAvailableThreadCount(),
                null,
                array
        );
    }

    public ParallelOrigin(
            @NotNull final Class<E> clazz,
            @NotNull final E[] array,
            final int threadCount
    ) {
        super(
                clazz,
                threadCount,
                null,
                array
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelOrigin<E> setCoreCount(int coreCount) {
        return new ParallelOrigin<>(clazz, array, coreCount);
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
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelOrigin{array=" +
                Arrays.toString(array) +
                ", cores="              +
                threadCount            +
                "}";
    }
}
