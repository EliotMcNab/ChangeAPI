package com.company.utilities;

import com.company.collections.changeAPI.annotations.Sorted;
import com.company.utilities.comparators.ArrayElementComparator;
import com.company.utilities.comparators.ObjectComparator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class ArrayUtil {

    @SafeVarargs
    public static Object[] blend(
            @NotNull final Object[]... arrays
    ) {
        Objects.requireNonNull(arrays);
        if (arrays.length == 0) return new Object[0];

        for (int i = 0; i < arrays.length - 1; i++) {
            if (arrays[i].length != arrays[i + 1].length)
                throw new IllegalArgumentException("All arrays must have equal size to be blended");
        }

        final Object[] result = new Object[arrays[0].length * arrays.length];

        for (int i = 0; i < arrays[0].length; i++) {
            for (int j = 0; j < arrays.length; j++) {
                result[i * arrays.length + j] = arrays[j][i];
            }
        }

        return result;
    }

    public static <T> int stepSearch(
            @Sorted final T[] array,
            @Nullable final T toFind,
            @NotNull final Comparator<T> comparator,
            final int stepSize
    ) {
        int minIndex, maxIndex;
        minIndex = 0;
        maxIndex = stepSize;

        while (maxIndex < array.length && comparator.compare(array[maxIndex], toFind) < 0) {
            minIndex = maxIndex;
            maxIndex += stepSize;
        }

        return Arrays.binarySearch(array, minIndex, Math.min(maxIndex, array.length), toFind, comparator);
    }

    /**
     * Partitions an array into several sections. If the array is smaller than the number of required partitions,
     * will return as many partitions as the array has elements instead.
     * @param array ({@code Object[]}): the array to partition
     * @param partitionCount ({@code int}): the amount of partitions
     * @return (int[][]): start (inclusive) & stop (exclusive) index for every array partition
     */
    public static int[][] partition(
            @NotNull final Object[] array,
            final int partitionCount
    ) {
        Objects.requireNonNull(array);
        if (array.length == 0 | partitionCount == 0) return new int[0][2];
        if (partitionCount == 1) return new int[][]{{0, array.length}};

        // determines the length of the array
        final int length = array.length;
        // creates the resulting array depending on whether array has fewer elements than required partitions
        final int[][] partitions = new int[Math.min(length, partitionCount)][2];

        // array has fewer elements than required partitions...
        if (length < partitionCount) {
            // returns as many partitions as the array has elements, one partition per element
            for (int i = 0; i < partitions.length; i++) {
                partitions[i] = new int[]{i, i + 1};
            }
        }
        // array has more elements that required partitions...
        else {
            // divides the array into the amount of partitions
            final int step = length / partitionCount;
            // saves the indexes of each partition
            for (int i = 0; i < partitions.length - 1; i++) {
                partitions[i] = new int[]{step * i, step * i + step};
            }
            // makes sure the last partition extends to the end of the array
            // (this can result in this partition being longer when the number of array elements is not a multiple
            // of the required amount of partitions)
            partitions[partitionCount - 1] = new int[]{(partitions.length - 1) * step, length};
        }

        // returns the final partitions
        return partitions;
    }

    /**
     * Partitions an array into several sections. If the array is smaller than the number of required partitions,
     * will return as many partitions as the array has elements instead.
     * @param array ({@code int[]}): the array to partition
     * @param partitionCount ({@code int}): the amount of partitions
     * @return (int[][]): start (inclusive) & stop (exclusive) index for every array partition
     */
    public static int[][] partition(
            final int @NotNull [] array,
            final int partitionCount
    ) {
        Objects.requireNonNull(array);
        if (array.length == 0 | partitionCount == 0) return new int[0][2];
        if (partitionCount == 1) return new int[][]{{0, array.length}};

        // determines the length of the array
        final int length = array.length;
        // creates the resulting array depending on whether array has fewer elements than required partitions
        final int[][] partitions = new int[Math.min(length, partitionCount)][2];

        // array has fewer elements than required partitions...
        if (length < partitionCount) {
            // returns as many partitions as the array has elements, one partition per element
            for (int i = 0; i < partitions.length; i++) {
                partitions[i] = new int[]{i, i + 1};
            }
        }
        // array has more elements that required partitions...
        else {
            // divides the array into the amount of partitions
            final int step = length / partitionCount;
            // saves the indexes of each partition
            for (int i = 0; i < partitions.length - 1; i++) {
                partitions[i] = new int[]{step * i, step * i + step};
            }
            // makes sure the last partition extends to the end of the array
            // (this can result in this partition being longer when the number of array elements is not a multiple
            // of the required amount of partitions)
            partitions[partitionCount - 1] = new int[]{(partitions.length - 1) * step, length};
        }

        // returns the final partitions
        return partitions;
    }

    /**
     * Determines the total length of a 2D array
     * @param array ({@code Object[][]}): array to determine the total length of
     * @return (int) sum of the length of each array contained in the given array
     */
    public static int determineTotalLength(
            @NotNull final Object[][] array
    ) {
        Objects.requireNonNull(array);
        if (array.length == 0) return 0;

        // TODO: make this work for n-dimensional arrays
        int length = 0; // initialises the length

        // adds up the length of every array in the given array
        for (Object[] objects : array) {
            length += objects.length;
        }

        // returns the final length
        return length;
    }

    /**
     * Finds the indexes of all values matching the given predicate
     * @param array ({@code T[]}): array of values to check against predicate
     * @param filter ({@code Predicate<? super T>}): predicate to check against values
     * @return (boolean): index of all values matching the predicate
     * @param <T> type of the array
     */
    public static <T> int[] findAllMatches(
            @NotNull final T[] array,
            @NotNull final Predicate<? super T> filter
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(filter);

        // initialises index of matching values
        final int[] result = new int[array.length];

        int k = 0;
        // for every value in the array...
        for (int i = 0; i < array.length; i++) {
            // ...checks to see if it matches the given predicate
            if (filter.test(array[i])) result[k++] = i;
        }

        // returns the final array of indexes of matches
        return Arrays.copyOf(result, k);
    }

    public static <T> int findFirstMatch(
            @NotNull final T[] array,
            @NotNull final Predicate<? super T> filter
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(filter);

        for (int i = 0; i < array.length; i++) {
            if (filter.test(array[i])) return i;
        }

        return -1;
    }

    public static int[] countMatches(
            @NotNull final Object[] array,
            @NotNull final Object[] toFind
    ) {
        return countMatchesImpl(Arrays.copyOf(array, array.length), toFind);
    }

    @Contract(mutates = "param1")
    public static int[] countMatchesImpl(
            @NotNull final Object[] array,
            @NotNull final Object[] toFind
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(toFind);

        final Comparator<Object> comparator = new ObjectComparator();
        final int[] count = new int[toFind.length];

        Arrays.parallelSort(array, comparator);

        for (int i = 0; i < array.length; i++) {
            final int index = Arrays.binarySearch(toFind, array[i], comparator);
            if (index >= 0) count[index]++;
        }

        return count;
    }

    /**
     * Gets values at indexes which are multiples of the given number
     * @param array  ({@code T[]}): array to get the values of
     * @param num    ({@code int}): gets values at indexes which are multiples of this number
     * @param <T>    type of the array
     * @return (T[]): values at indexes which are multiples of the given number
     */
    public static <T> T[] getAtMultiples(
            @NotNull final T[] array,
            final int num
    ) {
        return getAtMultiplesImpl(array, num, false);
    }

    /**
     * Gets values at indexes which are not multiples of the given number
     * @param array  ({@code T[]}): array to get the values of
     * @param num    ({@code int}): gets values at indexes which are not multiples of this number
     * @param <T>    type of the array
     * @return (T[]): values at indexes which are not multiples of the given number
     */
    public static <T> T[] getAtNonMultiples(
            @NotNull final T[] array,
            final int num
    ) {
        return getAtMultiplesImpl(array, num, true);
    }

    /**
     * Implementation algorithm for getAtMultiples & getAtNonMultiples
     * @param array ({@code T[]}): array to get the values of
     * @param num ({@code int}): gets values at indexes which are multiples or not of this number
     * @param invert ({@code boolean}): if true, gets values at indexes which are <strong>not</strong> multiples of the
     *               given number
     * @return (T[]): values at indexes which are multiples or not of the given number
     * @param <T> type of the array
     */
    private static <T> T[] getAtMultiplesImpl(
            @NotNull final T[] array,
            final int num,
            final boolean invert
    ) {
        Objects.requireNonNull(array);
        if (array.length == 0) return array;

        // creates the resulting array
        final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length / num);

        int k = 0;
        // for every element in the array...
        for (int i = 0; i < array.length; i++) {
            // ...if it is a multiple of the given number and should be added, saves it
            if ((i % num == 0) != invert) result[k++] = array[i];
        }

        // returns the final array of multiples or non-multiples
        return Arrays.copyOf(result, k);
    }

    /**
     * swaps both values at the given indexes
     * @param array ({@code Object[]}): array containing the values to swap
     * @param a ({@code int}): index of the first value
     * @param b ({@code int}): index of the second value
     */
    @Contract(mutates = "param1")
    public static void swap(
            @NotNull final Object[] array,
            final int a,
            final int b
    ) {
        Objects.requireNonNull(array);

        // saves the first value
        final Object temp = array[a];
        // swaps both values
        array[a] = array[b];
        array[b] = temp;
    }

    @Contract(mutates = "param1")
    public static int[] retainDistinct(
            final int @NotNull [] array
    ) {
        return retainDistinct(array, array.length);
    }

    @Contract(mutates = "param1")
    public static int[] retainDistinct(
            final int @NotNull [] array,
            final int to
    ) {
        Objects.requireNonNull(array);
        if (array.length == 0) return array;

        // sorts the given array
        Arrays.parallelSort(array);
        // initialises the result array
        final int[] blindResult = new int[array.length];

        // first element is always considered as unique
        blindResult[0] = array[0];

        int i, k;
        // iterates through every remaining element in the array...
        for (i = 1, k = 1; i < to; i++) {
            // ...keeps only the elements which are distinct
            if (array[i] != array[i-1]) blindResult[k++] = array[i];
        }

        // returns the final array of unique values
        return Arrays.copyOf(blindResult, k);
    }

    /**
     * Keeps only the distinct elements in an array
     * @param array ({@code T[]}): array to check the values of
     * @return (T[]): distinct elements in the array
     * @param <T> class of the array
     */
    public static <T> T[] retainDistinct(
            @NotNull final T [] array
    ) {
        return retainDistinct(array, (Comparator<T>) new ObjectComparator());
    }

    /**
     * Keeps only the distinct elements in an array
     * @param array ({@code T[]}): array to check the values of
     * @param comparator ({@code Comparator<T>}): used to compare values inside the array
     * @return (T[]): distinct elements in the array
     * @param <T> class of the array
     */
    public static <T> T[] retainDistinct(
            @NotNull final T[] array,
            @NotNull final Comparator<T> comparator
    ) {
        Objects.requireNonNull(comparator);
        Objects.requireNonNull(array);

        if (array.length == 0) return array;

        // wraps array values to their indexes to be able to retrieve them after sorting
        final Object[][] wrapped = ArrayUtil.wrapArrayIndexes(array);
        // keeps only the distinct elements in the wrapped array but sorts it
        final Object[][] distinct = ArrayUtil.retainDistinctImpl(
                wrapped,
                (ArrayElementComparator<Object>) new ArrayElementComparator<>(0, comparator)
        );

        // retrieves the distinct elements in the array in their correct order
        return retrieveDistinct((Class<T>) array.getClass().getComponentType(), distinct, comparator);
    }

    /**
     * Keeps only the distinct elements in an array <strong>and sorts it</strong>
     * @param array ({@code T[]}): array to check the values of
     * @param comparator ({@code Comparator<T>}): used to compare values inside the array
     * @return (T[]): distinct elements in the array
     * @param <T> class of the array
     */
    public static <T> T[] retainDistinctImpl(
            @NotNull final T[] array,
            @NotNull final Comparator<T> comparator
    ) {
        Objects.requireNonNull(comparator);
        Objects.requireNonNull(array);

        if (array.length == 0) return array;

        // sorts the given array
        Arrays.parallelSort(array, comparator);
        // initialises the blind result array
        final T[] blindResult = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);

        // first element in the given array is always considered to be unique
        blindResult[0] = array[0];

        int k = 1;
        // iterates through every other element in the array...
        for (int i = 1; i < array.length; i++) {
            // ...retains them only if they are different from other elements
            if (comparator.compare(array[i - 1], array[i]) != 0) blindResult[k++] = array[i];
        }

        // returns the final resulting array
        return Arrays.copyOf(blindResult, k);
    }

    public static int[] retainDistinctImpl(
            final int @NotNull [] array
    ) {
        Objects.requireNonNull(array);

        if (array.length == 0) return array;

        // sorts the given array
        Arrays.parallelSort(array);
        // initialises the blind result array
        final int[] blindResult = new int[array.length];

        // first element in the given array is always considered to be unique
        blindResult[0] = array[0];

        int k = 0;
        // iterates through every other element in the array...
        for (int i = 1; i < array.length; i++) {
            // ...retains them only if they are different from other elements
            if (array[i - 1] != array[i]) blindResult[k++] = array[i];
        }

        // returns the final resulting array
        return Arrays.copyOf(blindResult, k);
    }

    /**
     * Retrieves the distinct elements in their correct order in a wrapped array of distinct values
     * @param clazz ({@code Class<T>}): the class of elements in the original array
     * @param distinct ({@code Object[][]}): wrapped array of distinct values
     * @param comparator ({@code Comparator<T>}): used to compare values inside the array
     * @return (T[]): array of distinct values in their correct order relative to the original array
     * @param <T> the type of the original array
     */
    private static <T> T[] retrieveDistinct(
            @NotNull final Class<T> clazz,
            @NotNull final Object[][] distinct,
            @NotNull final Comparator<T> comparator
    ) {
        // initialises the resulting array
        final T[] result = (T[]) Array.newInstance(clazz, distinct.length);
        // sorts distinct values to retrieve original ordering
        Arrays.parallelSort(distinct, (ArrayElementComparator<Object>) new ArrayElementComparator<>(1, comparator));

        // copies over distinct values to the result array
        for (int i = 0; i < distinct.length; i++) {
            result[i] = (T) distinct[i][0];
        }

        // returns the final array of unique values
        return result;
    }

    /**
     * Searches in parallel for unique elements in the given array
     * @param array ({@code T[]}): array to check the values of
     * @param threadCount (int): maximum number of threads the algorithm can use
     * @return (T[]): distinct elements in the array
     * @param <T> class of the array
     */
    public static <T> T[] parallelRetainDistinct(
            @NotNull final T[] array,
            final int threadCount
    ) {
        return parallelRetainDistinct(array, threadCount, (Comparator<T>) new ObjectComparator());
    }

    /**
     * Searches in parallel for unique elements in the given array
     * @param array ({@code T[]}): array to check the values of
     * @param threadCount (int): maximum number of threads the algorithm can use
     * @param comparator ({@code Comparator<T>}): used to compare values inside the array
     * @return (T[]): distinct elements in the array
     * @param <T> class of the array
     */
    public static <T> T[] parallelRetainDistinct(
            @NotNull final T[] array,
            final int threadCount,
            @NotNull final Comparator<T> comparator
    ) {
        Objects.requireNonNull(comparator);
        Objects.requireNonNull(array);

        if (array.length == 0) return array;

        // wraps array values to their indexes to be able to retrieve them after sorting
        final Object[][] wrapped = ArrayUtil.wrapArrayIndexes(array);
        // keeps only the distinct elements in the wrapped array but sorts it
        final Object[][] distinct = ArrayUtil.parallelRetainDistinctImpl(
                wrapped,
                (ArrayElementComparator<Object>) new ArrayElementComparator<>(0, comparator),
                threadCount
        );

        // retrieves the distinct elements in the array in their correct order
        return retrieveDistinct((Class<T>) array.getClass().getComponentType(), distinct, comparator);
    }

    /**
     * Searches in parallel for unique elements in the given array <strong>and sorts the array</strong>
     * @param array ({@code T[]}): array to check the values of
     * @param comparator ({@code Comparator<T>}): used to compare values inside the array
     * @param threadCount (int): maximum number of threads the algorithm can use
     * @return (T[]): distinct elements in the array
     * @param <T> class of the array
     */
    public static <T> T[] parallelRetainDistinctImpl(
            @NotNull final T[] array,
            @NotNull final Comparator<T> comparator,
            final int threadCount
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(comparator);

        if (array.length == 0) return array;

        // sorts the given array
        Arrays.parallelSort(array, comparator);

        // initialises the result array
        final T[] blindResult = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);

        // partition the array into the given threads
        final int[][] partitions = partition(array, threadCount);
        final Thread[] threads = new Thread[partitions.length];

        // keeps track of where to add elements in the blind result array
        final AtomicInteger k = new AtomicInteger(0);

        // for every partition in the array...
        for (int i = 0; i < partitions.length; i++) {
            // ...gets the partition
            final int[] partition = partitions[i];

            // the task run by each thread
            final Runnable task = () -> {
                // makes sure we can compare the current value to the value before it
                final int start = Math.max(partition[0], 1);

                // for every element in the current thread's partition of the array...
                for (int j = start; j < partition[1]; j++) {
                    // ...if the element is different from the previous element, adds it
                    if (comparator.compare(array[j - 1], array[j]) != 0) blindResult[k.getAndIncrement()] = array[j];
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

        // returns the resulting array
        return Arrays.copyOf(blindResult, k.get());
    }

    public static int[] parallelRetainDistinctImpl(
            final int @NotNull [] array,
            final int threadCount
    ) {
        Objects.requireNonNull(array);

        if (array.length == 0) return array;

        // sorts the given array
        Arrays.parallelSort(array);

        // initialises the result array
        final int[] blindResult = new int[array.length];

        // partition the array into the given threads
        final int[][] partitions = partition(array, threadCount);
        final Thread[] threads = new Thread[partitions.length];

        // keeps track of where to add elements in the blind result array
        final AtomicInteger k = new AtomicInteger(0);

        // for every partition in the array...
        for (int i = 0; i < partitions.length; i++) {
            // ...gets the partition
            final int[] partition = partitions[i];

            // the task run by each thread
            final Runnable task = () -> {
                // makes sure we can compare the current value to the value before it
                final int start = Math.max(partition[0], 1);

                // for every element in the current thread's partition of the array...
                for (int j = start; j < partition[1]; j++) {
                    // ...if the element is different from the previous element, adds it
                    if (array[j - 1] != array[j]) blindResult[k.getAndIncrement()] = array[j];
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

        // returns the resulting array
        return Arrays.copyOf(blindResult, k.get());
    }

    /**
     * Retrieves the distinct elements in their correct order in a wrapped array of distinct values
     * @param clazz ({@code Class<T>}): the class of elements in the original array
     * @param distinct ({@code Object[][]}): wrapped array of distinct values
     * @param comparator ({@code Comparator<T>}): used to compare values inside the array
     * @param threadCount ({@code int}): maximum number of threads the algorithm can use
     * @return (T[]): array of distinct values in their correct order relative to the original array
     * @param <T> the type of the original array
     */
    private static <T> T[] parallelRetrieveDistinct(
            @NotNull final Class<T> clazz,
            @NotNull final Object[][] distinct,
            @NotNull final Comparator<T> comparator,
            final int threadCount
    ) {
        // initialises the resulting array
        final T[] result = (T[]) Array.newInstance(clazz, distinct.length);
        // sorts distinct values to retrieve original ordering
        Arrays.parallelSort(distinct, (ArrayElementComparator<Object>) new ArrayElementComparator<>(1, comparator));

        // partitions the array of distinct values according to the number of available threads
        final int[][] partitions = partition(distinct, threadCount);
        final Thread[] threads = new Thread[partitions.length];

        // for every partition of the array of distinct values...
        for (int i = 0; i < partitions.length; i++) {
            // ...gets the partition
            final int[] partition = partitions[i];

            // the task executed by each thread
            final Runnable task = () -> {
                // for every element in the partition...
                for (int j = partition[0]; j < partition[1]; j++) {
                    // ...saves the distinct element to its correct place in the result array
                    result[j] = (T) distinct[j][0];
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

        // returns the final array of unique values
        return result;
    }

    /**
     * Creates a new array with only the elements at the specified indexes
     * @param array ({@code T[]}): array containing the values to retain
     * @param indexes ({@code int[]}): indexes of the values to retain
     * @return array containing only the values to retain
     * @param <T> type of the array
     */
    @Contract(mutates = "param2")
    public static <T> T[] retainAt(
            final T @NotNull [] array,
            final int @NotNull [] indexes
    ) {
        // checks the validity of the arguments
        Objects.requireNonNull(array);
        Objects.requireNonNull(indexes);
        if (array.length == 0) return array;

        // sorts the given indexes
        Arrays.parallelSort(indexes);

        // creates the blind result array
        final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);

        // iterates over the indexes to keep
        int k = 0;
        for (int i : indexes) {
            // if the index is valid, adds it to the result array
            if (i >= 0 && i < array.length) result[k++] = array[i];
        }

        // returns the final result
        return Arrays.copyOf(result, k);
    }

    /**
     * Creates a new array without the elements at the specified indexes
     * @param array ({@code T[]}): array containing the values to remove
     * @param indexes ({@code int[]}): indexes of the values to remove
     * @return array containing without the elements to remove
     * @param <T> type of the array
     */
    @Contract(mutates = "param2")
    public static <T> T[] removeAt(
            final T @NotNull [] array,
            final int @NotNull [] indexes
    ) {
        return removeAt(array, indexes, indexes.length);
    }

    @Contract(mutates = "param2")
    public static <T> T[] removeAt(
            final T @NotNull [] array,
            final int @NotNull [] indexes,
            final int to
    ) {
        // checks the validity of the arguments
        Objects.requireNonNull(array);
        Objects.requireNonNull(indexes);
        if (array.length == 0) return array;

        // creates the blind result array
        final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);
        Arrays.parallelSort(indexes);

        // iterates over the indexes to remove
        int lastIndex = 0, k = 0;
        for (int i : retainDistinct(indexes, to)) {
            // if the index is negative (ie: corresponds to an element which wasn't found)
            // or greater than the size of the array, moves on
            if (i < 0 || i >= array.length) continue;

            // copies the elements between the previous index to remove and the current index to remove
            final int copyLength = i - k;
            System.arraycopy(array, k, result, lastIndex, copyLength);
            k += copyLength + 1;
            lastIndex += copyLength;
        }

        // copies over any remaining elements after the last index to remove
        System.arraycopy(array, k, result, lastIndex, array.length - k);
        lastIndex += array.length - k;

        // returns the resulting array
        return Arrays.copyOf(result, lastIndex);
    }

    /**
     * Concatenates multiple arrays together
     * @param arrays ({@code T[]...}): arrays to concatenate
     * @return resulting array concatenation
     * @param <T> class of the array
     */
    @SafeVarargs
    public static <T> T[] concatenate(
            @NotNull final T []... arrays
    ) {
        Objects.requireNonNull(arrays);

        // determines the combined length of every array
        int totalLength = 0;
        for (T[] array : arrays) {
            totalLength += array.length;
        }

        final Class<?> clazz = arrays.getClass().componentType().componentType(); // gets the array's class
        final T[] result = (T[]) Array.newInstance(clazz, totalLength);       // initialises the result array

        int k = 0;
        // for every array...
        for (T[] array : arrays) {
            // ...copies its elements over to the resulting array
            System.arraycopy(array, 0, result, k, array.length);
            k += array.length;
        }

        // returns the final concatenated array
        return result;
    }

    /**
     * Concatenates multiple arrays together
     * @param arrays ({@code int[]...}): arrays to concatenate
     * @return resulting array concatenation
     */
    public static int[] concatenate(
            final int @NotNull []... arrays
    ) {
        Objects.requireNonNull(arrays);

        // determines the combined length of every array
        int totalLength = 0;
        for (int[] array : arrays) {
            totalLength += array.length;
        }

        // initialise the result array
        final int[] result = new int[totalLength];

        int k = 0;
        // for every array...
        for (int[] array : arrays) {
            // ...copies its values over to the result array
            System.arraycopy(array, 0, result, k, array.length);
            k += array.length;
        }

        // returns the final resulting array
        return result;
    }

    /**
     * Finds the first occurrence of the given values in an array
     * @param array ({@code Object[]}): array to search
     * @param toFind ({@code Object[]}): values to find
     * @return (int[]): indexes of the first occurrence of each value
     */
    @Contract(mutates = "param2")
    public static int[] quickFindFirst(
            @NotNull final Object[] array,
            @NotNull final Object[] toFind
    ) {
        return quickFindFirst(array, toFind, new ObjectComparator());
    }

    /**
     * Finds the first occurrence of the given values in an array
     * @param array ({@code Object[]}): array to search
     * @param toFind ({@code Object[]}): values to find
     * @param comparator ({@code Comparator<Object}): used to sort the values to find
     * @return (int[]): indexes of the first occurrence of each value
     */
    @Contract(mutates = "param2")
    public static int[] quickFindFirst(
            @NotNull final Object [] array,
            @NotNull final Object [] toFind,
            @NotNull final Comparator<Object> comparator
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(toFind);
        Objects.requireNonNull(comparator);

        final Object[] uniqueToFind = retainDistinctImpl(toFind, comparator);   // keeps only the unique value to find
        final int[] indexes = new int[uniqueToFind.length];         // initialises the array of indexes
        Arrays.fill(indexes, -1);                                       // by default no value has been found

        // sorts the values to find to perform binary search on them
        Arrays.parallelSort(uniqueToFind, comparator);

        // for every element in the array...
        for (int i = 0; i < array.length; i++) {
            // ...checks to see if it matches a values to find...
            final int index = Arrays.binarySearch(uniqueToFind, array[i], comparator);
            // ...and if the current index is the smallest for that value so far...
            if (index >= 0 && (indexes[index] < 0 || i < indexes[index])) {
                // ...saves it
                indexes[index] = i;
            }
        }

        // returns the final array of indexes
        return indexes;
    }

    /**
     * Searches in parallel for the first occurrence of the given values in an array, using all available cores
     * @param array ({@code Object[]}): array to search
     * @param toFind ({@code Object[]}): values to find
     * @return (int[]): indexes of the first occurrence of each value
     */
    @Contract(mutates = "param2")
    public static <T> int[] parallelQuickFindFirst(
            @NotNull final T [] array,
            @NotNull final T [] toFind
    ) {
        return parallelQuickFindFirst(array, toFind, new ObjectComparator(), Runtime.getRuntime().availableProcessors());
    }

    /**
     * Searches in parallel for the first occurrence of the given values in an array
     * @param array ({@code Object[]}): array to search
     * @param toFind ({@code Object[]}): values to find
     * @param coreCount ({@code int}): the amount of cores available to parallelize
     * @return (int[]): indexes of the first occurrence of each value
     */
    @Contract(mutates = "param2")
    public static <T> int[] parallelQuickFindFirst(
            @NotNull final T[] array,
            @Sorted @NotNull final T[] toFind,
            final int coreCount
    ) {
        return parallelQuickFindFirst(array, toFind, new ObjectComparator(), coreCount);
    }

    /**
     * Searches in parallel for the first occurrence of the given values in an array
     * @param array ({@code Object[]}): array to search
     * @param toFind ({@code Object[]}): values to find
     * @param comparator ({@code Comparator<Object}): used to sort the values to find
     * @param coreCount ({@code int}): the amount of cores available to parallelize
     * @return (int[]): indexes of the first occurrence of each value
     */
    @Contract(mutates = "param2")
    public static <T> int[] parallelQuickFindFirst(
            @NotNull final T[] array,
            @NotNull final T[] toFind,
            @NotNull final Comparator<T> comparator,
            final int coreCount
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(toFind);
        Objects.requireNonNull(comparator);

        final T[] uniqueToFind = retainDistinctImpl(toFind, comparator);    // keeps only unique values to find
        final int[] indexes = new int[uniqueToFind.length];          // initialises the array of indexes
        final int[][] partitions = partition(array, coreCount);           // partitions the array according to the number of cores

        Arrays.fill(indexes, -1);                                       // by default no value has been found

        final Thread[] threads = new Thread[partitions.length];             // all threads to be used

        // for every partition in the array...
        for (int i = 0; i < partitions.length; i++) {
            // ...gets the partition
            final int[] partition = partitions[i];

            // task to execute inside each thread
            final Runnable task = () -> {
                // for every element in this thread's partition of the given array...
                for (int j = partition[0]; j < partition[1]; j++) {
                    // ...looks for the element...
                    final int index = Arrays.binarySearch(uniqueToFind, array[j], comparator);
                    // ...and if the current index is the smallest found so far...
                    if (index >= 0 && (indexes[index] < 0 || j < indexes[index])) {
                        // ...saves it
                        indexes[index] = j;
                    }
                }
            };

            // starts the thread
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

        // returns the indexes of all first occurrences
        return indexes;
    }

    public static <T> int[] quickFindAll(
            @NotNull final T[] array,
            final Object[] toFind
    ) {
        return quickFindAll(array, toFind, new ObjectComparator());
    }

    /**
     * Finds all occurrence of given values in an array
     * @implNote optimised for very large arrays
     * @param array ({@code Object[]}): array to search
     * @param toFind ({@code Collection\u003C ? \u003E}): values to find
     * @return (int[]): index of the values in the array
     */
    @Contract(mutates = "param2")
    public static <T> int[] quickFindAll(
            @NotNull final T[] array,
            @NotNull final Object[] toFind,
            @NotNull final Comparator<Object> comparator
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(toFind);
        Objects.requireNonNull(comparator);

        final Object[] uniqueToFind = retainDistinctImpl(toFind, comparator);   // keeps only unique values to find
        final int[] indexes = new int[array.length];                    // initialises array of found indexes

        Arrays.parallelSort(uniqueToFind, comparator);                          // sorts the values to find

        int k = 0;
        // for every element in the array...
        for (int i = 0; i < array.length; i++) {
            // ...tries to find the element...
            final int index = Arrays.binarySearch(uniqueToFind, array[i], comparator);
            // ...and if it found, saves it
            if (index >= 0) indexes[k++] = i;
        }

        // returns the resulting array of indexes
        return Arrays.copyOf(indexes, k);
    }

    /**
     * Wraps array values in a 2D array mapping the original value to its index
     * (useful when an algorithm requires a sorted array, but it is necessary to reverse the sorting to get results
     * relative to the original array)
     * @param array ({@code Object[]}): array to wrap
     * @return (Object[][]): 2D array mapping value to their index
     */
    // TODO: get rid of this and replace it with more efficient algorithms
    public static Object[][] wrapArrayIndexes(
            final Object[] array
    ) {
        final Object[][] result = new Object[array.length][2];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Object[]{array[i], i};
        }
        return result;
    }

    @SafeVarargs
    public static <T> T[][] wrapArrays(
            @NotNull final T[]... arrays
    ) {
        Objects.requireNonNull(arrays);
        final Class<?> arrayClass = arrays.getClass().getComponentType().getComponentType();
        if (arrays.length == 0) return (T[][]) Array.newInstance(arrayClass, 0, 0);

        for (int i = 0; i < arrays.length - 1; i++) {
            if (arrays[i].length != arrays[i+1].length) throw new IllegalArgumentException(
                    "Arrays must be of equal size to be wrapped"
            );
        }

        // final Object[][] result = new Object[arrays[0].length][arrays.length];
        final T[][] result = (T[][]) Array.newInstance(arrayClass, arrays[0].length, arrays.length);

        for (int i = 0; i < arrays[0].length; i++) {
            final T[] element = (T[]) Array.newInstance(arrayClass, arrays.length);
            for (int j = 0; j < arrays.length; j++) {
                element[j] = arrays[j][i];
            }
            result[i] = element;
        }

        return result;
    }

    /**
     * Removes the specified values from the array
     * @param array ({@code E[]}): array from which to remove the values
     * @param clazz ({@code Class\u003C E \u003E}): array element's class
     * @param c ({@code Collection \u003C ? \u003E}): values to remove
     * @return (E[]) array of retained values
     * @param <E> class of the array
     */
    public static <E> E[] batchRemove(
            @NotNull final E[] array,
            @NotNull final Class<E> clazz,
            @NotNull final Collection<?> c
    ) {
        return batchEdit(array, clazz, c, false);
    }

    /**
     * Keeps the specified values from the array
     * @param array ({@code E[]}): array from which to retain the values
     * @param clazz ({@code Class\u003C E \u003E}): array element's class
     * @param c ({@code Collection \u003C ? \u003E}): values to retain
     * @return (E[]) array of retained values
     * @param <E> class of the array
     */
    public static <E> E[] batchRetain(
            @NotNull final E[] array,
            @NotNull final Class<E> clazz,
            @NotNull final Collection<?> c
    ) {
        return batchEdit(array, clazz, c, true);
    }

    /**
     * Either keeps only the values in the array which are contained in the collection
     * or only those which are not part of the collection, depending on the retain mode
     * @param array ({@code E[]}): base array
     * @param clazz ({@code Class\u003C E \u003E}): array element's class
     * @param c ({@code Collection \u003C ? \u003E}): values to retain / remove
     * @param retain ({@code boolean}): retain mode (false = remove elements, true = keep elements)
     * @return (E[]) array of resulting values
     * @param <E> class of the array
     */
    private static <E> E[] batchEdit(
            @NotNull final E[] array,
            @NotNull final Class<E> clazz,
            @NotNull final Collection<?> c,
            final boolean retain
    ) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(c);

        // initialises the resulting array
        final E[] blindResult = (E[]) Array.newInstance(clazz, array.length);

        int i = -1, k = 0;
        // while the elements in the array matching the elements in the collection should be retained, skips them
        while (i < array.length - 1 && c.contains(array[k = ++i]) == retain);

        // copies over all the previous elements to be retained
        System.arraycopy(array, 0, blindResult, 0, k);

        // while there are still elements lefts to compare...
        while (++i < array.length) {
            // ...if the current element should be retained, adds it to the blind result
            if (c.contains(array[i]) == retain) blindResult[k++] = array[i];
        }

        // returns the resulting array
        return Arrays.copyOf(blindResult, k);
    }
}
