package com.company.collections.changeAPI.changes.parallel.remove;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ParallelRemoveAt<E> extends ParallelRemoveBase<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelRemoveAt(
            @NotNull final Class<E> clazz,
            final int @NotNull [] indexes,
            final int threadCount
    ) {
        super(
                clazz,
                threadCount,
                null,
                indexes
        );
    }

    public ParallelRemoveAt(
            @NotNull final Class<E> clazz,
            final int @NotNull [] indexes,
            final int threadCount,
            final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                null,
                indexes,
                parent
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelRemoveAt<E> setCoreCount(int coreCount) {
        return new ParallelRemoveAt<>(clazz, removalIndexes, coreCount);
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
        // retains only distinct values to remove
        final int[] uniqueToRemove = ArrayUtil.retainDistinct(removalIndexes);

        // partitions the given array according to the number of threads
        final int[][] indexPartitions = ArrayUtil.partition(uniqueToRemove, threadCount);
        // holds the result of each thread
        final E[][] partialResults = (E[][]) Array.newInstance(clazz.arrayType(), indexPartitions.length);
        final Thread[] threads = new Thread[indexPartitions.length];

        // for evey partition in the array...
        for (int i = 0; i < indexPartitions.length; i++) {
            final int[] partition = indexPartitions[i]; // gets the partition
            final int indexI = i;                  // saves the partition index for use in Runnable

            // task executed in each thread
            final Runnable task = () -> {
                // the partial result in this thread
                final E[] threadResult = (E[]) Array.newInstance(clazz, array.length);

                // determines where to start and where to stop copying
                final int start, stop;
                if (indexI > 0) {
                    final int[] previousPartition = indexPartitions[indexI - 1];
                    start = uniqueToRemove[previousPartition[previousPartition.length - 1]];
                } else {
                    start = 0;
                }
                if (indexI < indexPartitions.length - 1) {
                    final int[] nextPartition = indexPartitions[indexI + 1];
                    stop = uniqueToRemove[nextPartition[0]];
                } else {
                    stop = array.length;
                }

                int k = start;
                int lastIndex = 0;
                // for every index in the sub-array of indexes to remove...
                for (int j = partition[0]; j < partition[1]; j++) {
                    // ...copies over the value between the previous index and the current index
                    System.arraycopy(array, k, threadResult, lastIndex, uniqueToRemove[j] - k);
                    lastIndex += uniqueToRemove[j] - k;
                    // skips the current index
                    k = uniqueToRemove[j] + 1;
                }

                // copies over trailing values after the last index and before the smallest index in the next sub-array
                System.arraycopy(array, k, threadResult, lastIndex, stop - k);
                lastIndex += stop - k;

                // saves the thread's results
                partialResults[indexI] = Arrays.copyOf(threadResult, lastIndex);
            };

            // creates and starts the thread
            threads[i] = new Thread(task);
            threads[i].start();
        }

        // waits for all threads to have finished
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // returns the sum of all partial results computed by each thread
        return ArrayUtil.concatenate(partialResults);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelRemoveAt{indexes=" +
                Arrays.toString(removalIndexes) +
                ", threads=" +
                threadCount +
                "}";
    }
}
