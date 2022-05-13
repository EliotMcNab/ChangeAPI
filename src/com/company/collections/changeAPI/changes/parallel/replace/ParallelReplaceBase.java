package com.company.collections.changeAPI.changes.parallel.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.parallel.ConditionalParallelChange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class ParallelReplaceBase<E> extends ConditionalParallelChange<E> {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Object[] values;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelReplaceBase(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @Nullable final Object[] values,
            @Nullable final Predicate<? super E> filter
    ) {
        super(
                clazz,
                threadCount,
                filter
        );
        this.values = values;
    }

    public ParallelReplaceBase(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @Nullable final Object[] values,
            @Nullable final Predicate<? super E> filter,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                filter,
                parent
        );
        this.values = values;
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public Object[] getValues() {
        return values;
    }
}
