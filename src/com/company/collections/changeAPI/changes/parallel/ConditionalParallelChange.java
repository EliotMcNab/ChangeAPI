package com.company.collections.changeAPI.changes.parallel;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.Conditional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class ConditionalParallelChange<E> extends ParallelChange<E> implements Conditional {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Predicate<? super E> filter;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ConditionalParallelChange(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @Nullable final Predicate<? super E> filter
    ) {
        super(
                clazz,
                threadCount
        );
        this.filter = filter;
    }

    public ConditionalParallelChange(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @Nullable final Predicate<? super E> filter,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                parent
        );
        this.filter = filter;
    }

    // ====================================
    //             CONTENTS
    // ====================================

    @Override
    public boolean matches(Object o) {
        try {
            return filter.test((E) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean allMatch(Collection<?> c) {
        for (Object o : c) {
            if (!matches(o)) return false;
        }
        return true;
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public Predicate<? super E> getFilter() {
        return filter;
    }
}
