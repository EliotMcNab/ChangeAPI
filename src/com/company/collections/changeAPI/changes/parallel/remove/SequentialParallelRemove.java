package com.company.collections.changeAPI.changes.parallel.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.parallel.ParallelChange;
import com.company.utilities.ArrayUtil;
import com.company.utilities.comparators.ObjectComparator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class SequentialParallelRemove<E> extends ParallelRemoveBase<E> {

    // =====================================
    //               FIELDS
    // =====================================

    private final Change<E>[] changes;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public SequentialParallelRemove(
            @NotNull final Class<E> clazz,
            final int threadCount,
            @NotNull final Change<E>[] changes
    ) {
        super(
                clazz,
                threadCount,
                null,
                null
        );
        this.changes = changes;
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    @Override
    public ParallelChange<E> setCoreCount(int coreCount) {
        return new SequentialParallelRemove<>(clazz, coreCount, changes);
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

    private Change<E>[][] separateChanges(final Change<E>[] changes) {
        final Change<E>[][] seperated = new Change[2][];
        final Change<E>[] removeAll = new Change[changes.length];
        final Change<E>[] removeFirst = new Change[changes.length];

        int kAll = 0;
        int kFirst = 0;

        for (Change<E> change : changes) {
            switch (change) {
                case ParallelRemoveAll e   -> removeAll[kAll++] = change;
                case ParallelRemoveFirst e -> removeFirst[kFirst++] = change;
                default                    -> throw new IllegalArgumentException("Unhandled change class " + change);
            }
        }

        seperated[0] = Arrays.copyOf(removeAll, kAll);
        seperated[1] = Arrays.copyOf(removeFirst, kFirst);

        return seperated;
    }

    private Object[] concatenateToRemove(final Change<E>[] changes) {
        int totalLength = 0;
        for (Change<E> change : changes) {
            totalLength += ((ParallelRemoveBase<E>) change).toRemove.length;
        }

        final Object[] allToRemove = new Object[totalLength];

        int lastIndex = 0;
        for (Change<E> change : changes) {
            final Object[] toRemove = ((ParallelRemoveBase<E>) change).toRemove;
            System.arraycopy(toRemove, 0, allToRemove, lastIndex, toRemove.length);
            lastIndex += toRemove.length;
        }

        return allToRemove;
    }

    @Override
    protected E[] applyToImpl(@NotNull E[] array) {
        final Change<E>[][] seperated = separateChanges(changes);
        final Change<E>[] removeAll = seperated[0];
        final Change<E>[] removeFirst = seperated[1];

        final Object[] removeAllValues = ArrayUtil.retainDistinct(concatenateToRemove(removeAll));
        final Object[] removeFirstValues = ArrayUtil.retainDistinct(concatenateToRemove(removeFirst));

        final Comparator<Object> comparator = new ObjectComparator();

        Arrays.parallelSort(removeAllValues, comparator);
        Arrays.parallelSort(removeFirstValues, comparator);

        final int[] removeAllIndexes = new int[array.length];
        final int[] removeFirstIndexes = new int[removeFirstValues.length];
        Arrays.fill(removeFirstIndexes, -1);

        final int[][] partitions = ArrayUtil.partition(array, threadCount);
        final Thread[] threads = new Thread[partitions.length];

        final AtomicInteger k = new AtomicInteger(0);
        for (int i = 0; i < partitions.length; i++) {
            final int[] partition = partitions[i];

            final Runnable task = () -> {
                for (int j = partition[0]; j < partition[1]; j++) {
                    final int indexAll = Arrays.binarySearch(removeAllValues, array[j], comparator);
                    if (indexAll >= 0)  {
                        removeAllIndexes[k.getAndIncrement()] = j;
                        continue;
                    }

                    if (removeFirstValues.length == 0) continue;

                    final int indexFirst = Arrays.binarySearch(removeFirstValues, array[j], comparator);
                    if (indexFirst >= 0 && (removeFirstIndexes[indexFirst] == -1 || j < removeFirstIndexes[indexFirst])) {
                        removeFirstIndexes[indexFirst] = j;
                    }
                }
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

        final int[] removeIndexes = ArrayUtil.concatenate(Arrays.copyOf(removeAllIndexes, k.get()), removeFirstIndexes);
        return ArrayUtil.removeAt(array, removeIndexes);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "SequentialParallelRemove{changes=" +
                Arrays.toString(changes) +
                ", threads=" +
                threadCount +
                "}";
    }
}
