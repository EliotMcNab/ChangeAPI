package com.company.collections.changeAPI.changes.parallel.retain;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ParallelRetainFirst<E> extends ParallelRetainBase<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelRetainFirst(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Object[] values
    ) {
        super(
                clazz,
                threadCount,
                values,
                null
        );
    }

    public ParallelRetainFirst(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Object[] values,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                values,
                null,
                parent
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelRetainFirst<E> setCoreCount(int coreCount) {
        return new ParallelRetainFirst<>(clazz, coreCount, values, getPreviousChange());
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
    protected E[] applyToImpl(@NotNull E[] array) {
        final int[] indexes = ArrayUtil.parallelQuickFindFirst(array, values, threadCount);
        return ArrayUtil.retainAt(array, indexes);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelRetainFirst{toRetain=" +
                Arrays.toString(values)        +
                ", threads="                   +
                threadCount                    +
                "}";
    }
}
