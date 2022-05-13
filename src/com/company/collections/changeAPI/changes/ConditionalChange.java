package com.company.collections.changeAPI.changes;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class ConditionalChange<E> extends SingleThreadChange<E> implements Conditional {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Predicate<? super E> filter;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ConditionalChange(
            final Class<E> clazz,
            final Predicate<? super E> filter
    ) {
        super(clazz);
        this.filter = filter;
    }

    public ConditionalChange(
            final Class<E> clazz,
            final Predicate<? super E> filter,
            final Change<E> parent
    ) {
        super(
                clazz,
                parent
        );
        this.filter = filter;
    }

    // ====================================
    //             CONTENTS
    // ====================================

    @Override
    public boolean matches(final Object o) {
        try {
            return filter.test((E) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean allMatch(final Collection<?> c) {
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
