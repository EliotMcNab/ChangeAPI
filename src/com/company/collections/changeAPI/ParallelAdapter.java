package com.company.collections.changeAPI;

import com.company.collections.changeAPI.changes.parallel.ParallelChange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParallelAdapter<E> extends ParallelChange<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private final Change<E> adapted;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelAdapter(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Change<E> adapted
    ) {
        super(
                clazz,
                threadCount
        );
        this.adapted = adapted;
    }

    public ParallelAdapter(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Change<E> adapted,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                parent
        );
        this.adapted = adapted;
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelAdapter<E> setCoreCount(int coreCount) {
        return new ParallelAdapter<>(clazz, coreCount, adapted, getPreviousChange());
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return adapted.canSequentialise(change);
    }

    @Override
    public Change<E> toSequential(Change<E>[] changes) {
        return adapted.toSequential(changes);
    }

    @Override
    protected E[] applyToImpl(@NotNull E[] array) {
        return adapted.applyToImpl(array);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelAdapter{change=" +
                adapted +
                "}";
    }
}
