package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.ConditionalChange;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class RemoveBase<E> extends ConditionalChange<E> {

    // ====================================
    //               FIELDS
    // ====================================

    // TODO: temporary, before finding proper array size to removal size ratio for big change
    protected static final int BIG_THRESHOLD = 700;

    protected final Object[] toRemove;
    protected final int[] removalIndexes;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RemoveBase(
            final Class<E> clazz,
            final Object[] toRemove,
            final Predicate<? super E> filter,
            final int[] removalIndexes
    ) {
        super(
                clazz,
                filter
        );
        this.toRemove = toRemove;;
        this.removalIndexes = removalIndexes;
    }

    public RemoveBase(
            final Class<E> clazz,
            final Object[] toRemove,
            final Predicate<? super E> filter,
            final int[] removalIndexes,
            final Change<E> parent
    ) {
        super(
                clazz,
                filter,
                parent
        );
        this.toRemove = toRemove;
        this.removalIndexes = removalIndexes;
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public Object[] getToRemove() {
        return Arrays.copyOf(toRemove, toRemove.length);
    }

    public Predicate<? super E> getFilter() {
        return filter;
    }

    // ====================================
    //              APPLYING
    // ====================================

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return new SequentialRemove<>(clazz, changes);
    }

    public boolean isBigChange(final E[] array) {
        return toRemove.length > BIG_THRESHOLD;
    }

}
