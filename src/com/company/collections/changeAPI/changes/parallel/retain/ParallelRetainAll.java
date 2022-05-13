package com.company.collections.changeAPI.changes.parallel.retain;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ObjectComparator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

public class ParallelRetainAll<E> extends ParallelRetainBase<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelRetainAll(
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

    public ParallelRetainAll(
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
    public ParallelRetainAll<E> setCoreCount(int coreCount) {
        return new ParallelRetainAll<>(clazz, coreCount, values, getPreviousChange());
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

    private E[][] getPartialResult(@NotNull E[] array) {
        final Comparator<Object> comparator = new ObjectComparator();

        final Object[] uniqueToRetain = ArrayUtil.retainDistinctImpl(values, comparator);
        Arrays.parallelSort(uniqueToRetain, comparator);

        final int[][] partitions = ArrayUtil.partition(array, threadCount);
        final Thread[] threads = new Thread[partitions.length];
        final E[][] partialResults = (E[][]) Array.newInstance(clazz.arrayType(), partitions.length);

        for (int i = 0; i < partitions.length; i++) {
            final int[] partition = partitions[i];
            final int lambdaI = i;

            final Runnable task = () -> {
                final E[] threadResult = (E[]) Array.newInstance(clazz, array.length);

                int k = 0;
                for (int j = partition[0]; j < partition[1]; j++) {
                    // TODO: try out exponential search, see if any performance is gained there
                    final int index = Arrays.binarySearch(uniqueToRetain, array[j], comparator);
                    if (index >= 0) threadResult[k++] = array[j];
                }

                partialResults[lambdaI] = Arrays.copyOf(threadResult, k);
            };

            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return partialResults;
    }

    @Override
    protected E[] applyToImpl(@NotNull E[] array) {
        return ArrayUtil.concatenate(getPartialResult(array));
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelRetainAll{toRetain=" +
                Arrays.toString(values)      +
                ", threads="                 +
                threadCount                  +
                "}";
    }
}
