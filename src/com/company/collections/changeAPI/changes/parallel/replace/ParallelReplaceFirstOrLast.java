package com.company.collections.changeAPI.changes.parallel.replace;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ArrayElementComparator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;

public class ParallelReplaceFirstOrLast<E> extends ParallelReplaceValues<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private final boolean replaceLast;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelReplaceFirstOrLast(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final E[] toReplace
    ) {
        super(
                clazz,
                threadCount,
                toReplace
        );
        this.replaceLast = false;
    }

    public ParallelReplaceFirstOrLast(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final E[] toReplace,
            final boolean replaceLast
    ) {
        super(
                clazz,
                threadCount,
                toReplace
        );
        this.replaceLast = replaceLast;
    }

    public ParallelReplaceFirstOrLast(
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
        this.replaceLast = false;
    }

    public ParallelReplaceFirstOrLast(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final E[] toReplace,
            final boolean replaceLast,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                threadCount,
                toReplace,
                parent
        );
        this.replaceLast = replaceLast;
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelReplaceFirstOrLast<E> setCoreCount(int coreCount) {
        return new ParallelReplaceFirstOrLast<>(clazz, coreCount, (E[]) values, getPreviousChange());
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

    /**
     * Maps the indexes of the first occurrence of every value to find with the replacing value at that index
     * @param array ({@code E[]}): array to replace the value of
     * @return (Object[][]): map of occurrence indexes to replacing values
     */
    private Object[][] getIndexMap(@NotNull E[] array) {
        // used to sort arrays and compare elements
        final Comparator<Object[]> comparator = new ArrayElementComparator<>(0);

        // maps the values to replace to their replacing values
        // and sorts them according to the values to replace
        final Object[][] wrapped = ArrayUtil.wrapArrays(getEvenIndexes(), getOddIndexes());
        Arrays.parallelSort(wrapped, comparator);

        // maps the indexes of found elements and the replacing values at those indexes
        // by default all indexes are not in the array and map to no value
        final Object[][] indexes = new Object[wrapped.length][2];
        Arrays.fill(indexes, new Object[]{-1, null});

        // partitions the array for each thread
        final int[][] arrayPartitions = ArrayUtil.partition(array, threadCount);
        final Thread[] threads = new Thread[arrayPartitions.length];

        // for every partition in the array...
        for (int i = 0; i < arrayPartitions.length; i++) {
            // ...gets the partition
            final int[] partition = arrayPartitions[i];

            // the task to be run by each thread
            final Runnable task = () -> {
                // for every element in this thread's partition of the array...
                for (int j = partition[0]; j < partition[1]; j++) {
                    // ...checks if that element should be replaced...
                    final int index = Arrays.binarySearch(wrapped, new Object[]{array[j]}, comparator);
                    // ...and is the first occurrence found so far...
                    if (index >= 0 && ((int) indexes[index][0] == -1 || j < (int) indexes[index][0]) != replaceLast) {
                        // ...if so saves the element's array and maps it to the replacing value at that index
                        indexes[index] = new Object[]{j, wrapped[index][1]};
                    }
                }
            };

            // creates and starts the next thread
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

        return indexes;
    }

    /**
     * Replaces the values at the indexes specified given index map by their mapped replacing values
     * @param array ({@code E[]}): array to replace the values of
     * @param indexMap ({@code Object[][]}): map of the first occurrence of every value to find to the replacing value
     *                 at that index
     * @return (E[]): array with all the indexes specified by the index map replaced by the mapped replacing values
     */
    private E[] replaceMapped(
            @NotNull final E[] array,
            @NotNull final Object[][] indexMap
    ) {
        // initialises the result array as a copy of the initial array
        final E[] result = Arrays.copyOf(array, array.length);

        // partitions the found indexes for each thread
        final int[][] indexPartitions = ArrayUtil.partition(indexMap, threadCount);
        final Thread[] threads = new Thread[indexPartitions.length];

        // for partition in the indexes...
        for (int i = 0; i < indexPartitions.length; i++) {
            // ...gets the partition
            final int[] partition = indexPartitions[i];

            // the task to be run by each thread
            final Runnable task = () -> {
                // for every element in this thread's partition of the index map...
                for (int j = partition[0]; j < partition[1]; j++) {
                    // ...gets the current index...
                    final int index = (int) indexMap[j][0];
                    // ...and if it is valid, replaces the value at that index with the mapped replacing value
                    if (index >= 0) result[index] = (E) indexMap[j][1];
                }
            };

            // creates and starts the next thread
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

        return result;
    }

    @Override
    protected E[] applyToImpl(@NotNull E[] array) {
        return replaceMapped(array, getIndexMap(array));
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ParallelReplaceFirstOrLast{toReplace=" +
                Arrays.toString(getEvenIndexes()) +
                ", replacing=" +
                Arrays.toString(getOddIndexes()) +
                ", threads=" +
                threadCount +
                "}";
    }
}
