package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.ConditionalChange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class ReplaceBase<E> extends ConditionalChange<E> {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Object[] values;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ReplaceBase(
            @NotNull final Class<E> clazz,
            @Nullable final Object[] toReplace,
            @Nullable final Predicate<? super E> filter
    ) {
        super(
                clazz,
                filter
        );
        this.values = toReplace;
    }

    public ReplaceBase(
            @NotNull final Class<E> clazz,
            @Nullable final Object[] toReplace,
            @Nullable final Predicate<? super E> filter,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                filter,
                parent
        );
        this.values = toReplace;
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return null;
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public Object[] getValues() {
        return values;
    }
}
