package com.company.collections.changeAPI.changes.parallel.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.parallel.ParallelChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ArrayElementComparator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;

public class ParallelReplaceAll<E> extends ParallelReplaceValues<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelReplaceAll(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final E[] toReplace
    ) {
        super(
                clazz,
                threadCount,
                toReplace
        );
    }

    public ParallelReplaceAll(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final E[] toReplace,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                toReplace,
                parent
        );
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelChange<E> setCoreCount(int coreCount) {
        return new ParallelReplaceAll<>(clazz, coreCount, (E[]) values, getPreviousChange());
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
        // used to sort and search for objects
        final Comparator<Object[]> comparator = new ArrayElementComparator<>(0);

        // maps each value to replace to its replacing value
        // and sorts the mapping array according to the replacing value
        final Object[][] wrapped = ArrayUtil.wrapArrays(getEvenIndexes(), getOddIndexes());
        Arrays.parallelSort(wrapped, comparator);

        // initialises the result array
        final E[] result = Arrays.copyOf(array, array.length);

        // partitions the array for each thread
        final int[][] partitions = ArrayUtil.partition(array, threadCount);
        final Thread[] threads = new Thread[partitions.length];

        // for every array partition...
        for (int i = 0; i < partitions.length; i++) {
            // ...gets the partition
            final int[] partition = partitions[i];

            // the task to run inside each thread
            final Runnable task = () -> {
                // iterates over the thread's partition of the array
                for (int j = partition[0]; j < partition[1]; j++) {
                    // searches for the current element in the array in the values to replace
                    final int index = Arrays.binarySearch(wrapped, new Object[]{result[j]}, comparator);
                    // if the element is found, replaces it with the correct value
                    if (index >= 0) result[j] = (E) wrapped[index][1];
                }
            };

            // creates and starts the thread
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

        // returns the final result computed by every thread
        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelReplaceAll{toReplace=" +
                Arrays.toString(getEvenIndexes()) +
                ", replacing=" +
                Arrays.toString(getOddIndexes()) +
                ", threads=" +
                threadCount +
                "}";
    }
}
