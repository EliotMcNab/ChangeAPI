package com.company.collections.changeAPI.changes.parallel.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.parallel.ParallelChange;

public abstract class ParallelRemoveBase<E> extends ParallelChange<E> {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Object[] toRemove;
    protected final int[] removalIndexes;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelRemoveBase(
            final Class<E> clazz,
            final int threadCount,
            final Object[] toRemove,
            final int[] removalIndexes
    ) {
        super(
                clazz,
                threadCount
        );
        this.toRemove = toRemove;
        this.removalIndexes = removalIndexes;
    }

    public ParallelRemoveBase(
            final Class<E> clazz,
            final int threadCount,
            final Object[] toRemove,
            final int[] removalIndexes,
            final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                parent
        );
        this.toRemove = toRemove;
        this.removalIndexes = removalIndexes;
    }

}
