package com.company.collections.changeAPI.changes.parallel.replace;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

public class ParallelReplaceAllIf<E> extends ParallelReplaceBase<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelReplaceAllIf(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Predicate<? super E> filter,
            @Nullable final E replacingValue
    ) {
        super(
                clazz,
                threadCount,
                new Object[]{replacingValue},
                filter
        );
    }

    public ParallelReplaceAllIf(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Predicate<? super E> filter,
            @Nullable final E replacingValue,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                new Object[]{replacingValue},
                filter,
                parent
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelReplaceAllIf<E> setCoreCount(int coreCount) {
        return new ParallelReplaceAllIf<>(clazz, coreCount, filter, (E) values[0], getPreviousChange());
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
        // initialises the result array as a copy of the given array
        final E[] result = Arrays.copyOf(array, array.length);

        // partitions the array for the specified number of threads
        final int[][] partitions = ArrayUtil.partition(array, threadCount);
        final Thread[] threads = new Thread[partitions.length];

        // for each array partition...
        for (int i = 0; i < partitions.length; i++) {
            // ...gets the partition
            final int[] partition = partitions[i];

            // the task to be run by each thread
            final Runnable task = () -> {
                // for each element in this thread's partition of the array...
                for (int j = partition[0]; j < partition[1]; j++) {
                    // ...checks the current element against the filter, and if it matches replaces it
                    if (filter.test(result[j])) result[j] = (E) values[0];
                }
            };

            // initialises and starts the next thread
            threads[i] = new Thread(task);
            threads[i].start();
        }

        // waits for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // returns the final result as computed by each thread
        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelReplaceAllIf{predicate=" +
                filter +
                ", replacing=" +
                values[0] +
                "}";
    }
}
