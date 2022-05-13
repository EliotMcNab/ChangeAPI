package com.company.collections.changeAPI.changes.parallel.remove;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ParallelRemoveFirst<E> extends ParallelRemoveBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
            ParallelRemoveAll.class,
            ParallelRemoveFirst.class
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelRemoveFirst(
            @NotNull final Class<E> clazz,
            @NotNull final Object[] toRemove,
            final int threadCount
    ) {
        super(
                clazz,
                threadCount,
                toRemove,
                null
        );
    }

    public ParallelRemoveFirst(
            @NotNull final Class<E> clazz,
            @NotNull final Object[] toRemove,
            final int threadCount,
            final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                toRemove,
                null,
                parent
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelRemoveFirst<E> setCoreCount(int coreCount) {
        return new ParallelRemoveFirst<>(clazz, toRemove, coreCount, getPreviousChange());
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return Arrays.asList(SEQUENTIALISEABLE).contains(change.getClass());
    }

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return new SequentialParallelRemove<>(clazz, threadCount, changes);
    }

    @Override
    protected E[] applyToImpl(@NotNull E[] array) {
        final int[] indexes = ArrayUtil.parallelQuickFindFirst(array, toRemove, threadCount);
        return ArrayUtil.removeAt(array, indexes);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelRemoveBase{toRemove=" +
                Arrays.toString(toRemove) +
                ", threads=" +
                threadCount +
                "}";
    }
}
