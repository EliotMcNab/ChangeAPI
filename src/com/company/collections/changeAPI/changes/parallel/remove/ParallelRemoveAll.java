package com.company.collections.changeAPI.changes.parallel.remove;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ObjectComparator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

public class ParallelRemoveAll<E> extends ParallelRemoveBase<E> {

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

    public ParallelRemoveAll(
            final Class<E> clazz,
            final Object[] toRemove,
            final int threadCount
    ) {
        super(
                clazz,
                threadCount,
                toRemove,
                null
        );
    }

    public ParallelRemoveAll(
            final Class<E> clazz,
            final Object[] toRemove,
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
    public ParallelRemoveAll<E> setCoreCount(int coreCount) {
        return new ParallelRemoveAll<>(clazz, toRemove, coreCount, getPreviousChange());
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
    protected E[] applyToImpl(E[] array) {
        // used for sorting and searching gor elements
        final Comparator<Object> comparator = new ObjectComparator();

        // keeps only the unique elements to remove and sorts them
        final Object[] uniqueToRemove = ArrayUtil.retainDistinctImpl(toRemove, new ObjectComparator());
        Arrays.parallelSort(uniqueToRemove, comparator);

        // splits the given array equally for each thread to access
        final int[][] partitions = ArrayUtil.partition(array, threadCount);

        // holds the result generated by each thread
        final E[][] partialResults = (E[][]) Array.newInstance(clazz.arrayType(), partitions.length);
        final Thread[] threads = new Thread[partitions.length];

        // for very split section of the array
        for (int i = 0; i < partitions.length; i++) {
            // gets the range of values the thread will be working of
            final int[] partition = partitions[i];

            // effectively final i for use inside the thread
            final int lambdaI = i;
            // task to execute inside each thread
            final Runnable task = () -> {
                // result found by this thread
                final E[] blindResult = (E[]) Array.newInstance(clazz, array.length);

                // for every value in the sub-array considered by the thread...
                int k = 0;
                for (int j = partition[0]; j < partition[1]; j++) {
                    final E value = array[j];
                    // ...checks if the curren value should be removed...
                    if (Arrays.binarySearch(uniqueToRemove, value, comparator) < 0) {
                        // ...otherwise saves it
                        blindResult[k++] = value;
                    }
                }

                // saves the result found by the thread
                partialResults[lambdaI] = Arrays.copyOf(blindResult, k);
            };

            // initialises and starts the thread
            threads[i] = new Thread(task);
            threads[i].start();
        }

        // waits for all the threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // returns the sum of all partial results found by each thread
        return ArrayUtil.concatenate(partialResults);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return null;
    }
}
