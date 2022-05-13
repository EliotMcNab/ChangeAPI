package com.company.collections.changeAPI.changes.parallel.replace;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParallelReplaceValues<E> extends ParallelReplaceBase<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelReplaceValues(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @Nullable final Object[] toReplace
    ) {
        super(
                clazz,
                threadCount,
                toReplace,
                null
        );
    }

    public ParallelReplaceValues(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @Nullable final Object[] toReplace,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                toReplace,
                null,
                parent
        );
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    public Object[] getEvenIndexes() {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException(
                    "Invalid array of elements to replace, " +
                            "must have equal number of values to replace and replacing values"
            );

        return ArrayUtil.getAtMultiples(values, 2);
    }

    public Object[] getOddIndexes() {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException(
                    "Invalid array of elements to replace, " +
                            "must have equal number of values to replace and replacing values"
            );

        return ArrayUtil.getAtNonMultiples(values, 2);
    }

}
