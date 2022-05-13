package com.company.collections.changeAPI.changes.singlethread.retain;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.ConditionalChange;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class RetainBase<E> extends ConditionalChange<E> {

    // ====================================
    //               FIELDS
    // ====================================
    protected final Object[] toRetain;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RetainBase(
            final Class<E> clazz,
            final Object[] toRetain,
            final Predicate<? super E> filter
    ) {
        super(
                clazz,
                filter
        );
        this.toRetain = toRetain;
    }

    public RetainBase(
            final Class<E> clazz,
            final Object[] toRetain,
            final Predicate<? super E> filter,
            final Change<E> parent
    ) {
        super(
                clazz,
                filter,
                parent
        );
        this.toRetain = toRetain;
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public Object[] getToRetain() {
        return Arrays.copyOf(toRetain, toRetain.length);
    }

    // ====================================
    //              APPLYING
    // ====================================

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return null;
    }

}
