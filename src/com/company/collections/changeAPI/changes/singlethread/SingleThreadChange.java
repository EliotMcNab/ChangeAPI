package com.company.collections.changeAPI.changes.singlethread;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.changes.singlethread.add.Add;
import com.company.collections.changeAPI.changes.singlethread.functions.FunctionalChange;
import com.company.collections.changeAPI.changes.singlethread.functions.Functions;
import com.company.collections.changeAPI.changes.singlethread.remove.RemoveAll;
import com.company.collections.changeAPI.changes.singlethread.remove.RemoveAt;
import com.company.collections.changeAPI.changes.singlethread.remove.RemoveFirst;
import com.company.collections.changeAPI.changes.singlethread.remove.RemoveIf;
import com.company.collections.changeAPI.changes.singlethread.replace.*;
import com.company.collections.changeAPI.changes.singlethread.retain.RetainAll;
import com.company.collections.changeAPI.changes.singlethread.retain.RetainFirst;
import com.company.collections.changeAPI.changes.singlethread.retain.RetainIf;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import com.company.collections.changeAPI.generation.Generator;
import com.company.collections.changeAPI.changes.singlethread.information.ChangeInformation;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SingleThreadChange<T> extends Change<T> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public SingleThreadChange(
            final Class<T> clazz
    ) {
        super(clazz);
    }

    public SingleThreadChange(
            final Class<T> clazz,
            final Change<T> parent
    ) {
        super(clazz, parent);
    }

    public SingleThreadChange(
            final Class<T> clazz,
            final Change<T> parent,
            final T[] array
    ) {
        super(clazz, parent, array);
    }

    // ====================================
    //               ADDING
    // ====================================

    /**
     * Adds the specified element to the {@link SingleThreadChange}. New elements are stored in a new {@link Add} instance which
     * <strong>only</strong> contains these elements and a reference to the previous SingleThreadChange
     * @param e ({@code T}): the element to add
     * @return (Add\u003C T \u003E): new SingleThreadChange containing the new element and instructions on how to add it
     */
    @Override
    public final Add<T> add(T e) {
        return new Add<>(clazz, (T[]) new Object[]{e}, this);
    }

    /**
     * Adds all the specified elements to the {@link SingleThreadChange}. New elements are stored in a new {@link Add} instance
     * which <strong>only</strong> contains these elements and a reference to the previous SingleThreadChange
     * @param elements ({@code T...}): elements to add
     * @return (Add\u003C T \u003E): new SingleThreadChange containing the new elements and instructions on how to add them
     */
    @SafeVarargs
    public final Add<T> addAll(T... elements) {
        return new Add<>(clazz, elements, this);
    }

    /**
     * Adds all the elements in the specified collection to the {@link SingleThreadChange}. New elements are stored in a new
     * {@link Add} instance which <strong>only</strong> contains these elements and a reference to the previous SingleThreadChange
     * @param c ({@code Collection<? extends T>}): collection of elements to add
     * @return (Add\u003C T \u003E): new SingleThreadChange containing the new elements and instructions on how to add them
     */
    @Override
    public final Add<T> addAll(Collection<? extends T> c) {
        return new Add<>(clazz, c, this);
    }

    public final Add<T> addAll(final Generator<T> generator, final int length) {
        return new Add<>(clazz, generator.generateArray(clazz, length), this);
    }

    // ====================================
    //              REMOVING
    // ====================================

    /**
     * Removes only the first instance of all the specified objects. The objects to remove are stored in a new {@link RemoveFirst}
     * instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): objects to remove
     * @return (RemoveFirst\u003C T \u003E): new SingleThreadChange containing all the elements to remove and instructions on how to remove them
     */
    public final RemoveFirst<T> removeFirst(Object... objects) {
        return new RemoveFirst<>(clazz, objects, this);
    }

    public final RemoveFirst<T> removeFirst(final int length, final Generator<T> generator) {
        return new RemoveFirst<>(clazz, generator.generateArray(clazz, length), this);
    }

    /**
     * Removes all the instances of all the specified objects. The objects to remove are stored in a new {@link RemoveAll}
     * instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): objects to remove
     * @return (RemoveFirst\u003C T \u003E): new SingleThreadChange containing all the elements to remove and instructions on how to remove them
     */
    public final RemoveAll<T> removeAll(Object... objects) {
        return new RemoveAll<>(clazz, objects, this);
    }

    /**
     * Removes all the instances of all the elements in the collection. The elements to remove are stored in a new {@link RemoveAll}
     * instance which only contains a reference to the previous change
     * @param c ({@code Collection<?> c}): collection containing the elements to remove
     * @return (RemoveFirst\u003C T \u003E): new SingleThreadChange containing all the elements to remove and instructions on how to remove them
     */
    @Override
    public final RemoveAll<T> removeAll(Collection<?> c) {
        return new RemoveAll<>(clazz, c, this);
    }

    public final RemoveAll<T> removeAll(final Generator<?> generator, final int length) {
        return new RemoveAll<>(clazz, ((Generator<Object>) generator).generateArray(Object.class, length) , this);
    }

    /**
     * Removes elements in the change if they match the given {@link Predicate}. The predicate is stored in a new {@link RemoveIf}
     * instance which only contains a reference to the previous change
     * @param filter ({@code Predicate<? super T>}): predicate used to filter out elements
     * @return (RemoveIf\u003C T \u003E): new SingleThreadChange containing the predicate used to remove elements and instructions on how to remove them
     */
    @Override
    public final RemoveIf<T> removeIf(Predicate<? super T> filter) {
        return new RemoveIf<>(clazz, filter, this);
    }

    /**
     * Removes elements on the change at the specified indexes. The indexes are stored in a new {@link RemoveAt} instance
     * which only contains a reference to the previous change
     * @param indexes ({@code int...}): indexes at which to remove the elements
     * @return (RemoveAt\u003C T \u003E): new change containing the indexes at which to remove elements and instructions on how to remove them
     */
    public final RemoveAt<T> removeAt(int... indexes) {
        return new RemoveAt<>(clazz, indexes, this);
    }

    public final RemoveAt<T> removeAt(final int length, final Generator<Integer> generator) {
        final int[] indexes = new int[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = generator.generate();
        }
        return new RemoveAt<>(clazz, indexes, this);
    }

    // ====================================
    //             REPLACING
    // ====================================

    /**
     * Replaces the elements at the specified indexes with their associated value.
     * <list>
     *     <li>Parameters at even positions must be the indexes at which to replace the elements</li>
     *     <li>Parameters at odd positions must be the values used to replace</li>
     * </list>
     * <br><u><i>example</i></u><br>
     * <pre>{@code
     * replaceAt(
     *      (int) index, (T) value,
     *      (int) index, (T) value,
     *      (int) index, (T) value
     * )
     * }</pre><br>
     * Indexes and elements to replace are stored in a new {@link ReplaceAt} instance which only contains a reference
     * to the previous change
     * @param objects ({@code Object...}): index-element pairs
     * @return (ReplaceAt\u003C T \u003E): new SingleThreadChange containing the indexes at which to replace values, the replacing
     * values and instructions on how to apply the change
     */
    public final ReplaceAt<T> replaceAt(Object... objects) {
        return new ReplaceAt<>(clazz, objects, this);
    }

    public final ReplaceAt<T> setAt(final int @NotNull [] indexes, final T @NotNull [] replacing) {
        return new ReplaceAt<>(clazz, indexes, replacing, this);
    }

    public final ReplaceAt<T> setAt(final int @NotNull [] indexes, final @NotNull T value) {
        final T[] replacing = (T[]) Array.newInstance(clazz, indexes.length);
        Arrays.fill(replacing, value);
        return new ReplaceAt<>(clazz, indexes, replacing, this);
    }

    /**
     * Replaces all elements in an array which match the given predicate with the specified replacing value. The Predicate
     * and the replacing value are stored in a new {@link ReplaceAllIf} instance which only contains a reference
     * to the previous change
     * @param filter ({@code Predicate<T>}): predicate used to determine which elements must be replaced
     * @param replacingValue ({@code T}): value used to replace elements which match the predicate
     * @return (ReplaceAllIf \ u003C T \ u003E): new SingleThreadChange containing the predicate, the replacing values and
     * instructions on how to apply the change
     */
    public final ReplaceAllIf<T> replaceAll(final Predicate<T> filter, final T replacingValue) {
        return new ReplaceAllIf<>(clazz, filter, replacingValue, this);
    }

    /**
     * Replaces all occurrence of the specified elements by the given values.
     * <list>
     *     <li>Parameters at even positions must be the values to replace</li>
     *     <li>Parameters at odd positions must be the replacing values</li>
     * </list>
     * <br><u><i>example</i></u><br>
     * <pre>{@code
     * replaceAll(
     *      (T) value to replace, (T) replacing value,
     *      (T) value to replace, (T) replacing value,
     *      (T) value to replace, (T) replacing value
     * )
     * }</pre><br>
     * Values to replace and replacing values are stored in a new {@link ReplaceAll} instance which only contains a
     * reference to the previous change
     * @param values ({@code T...}): value to replace - replacing values pairs
     * @return (ReplaceAt\u003C T \u003E): new SingleThreadChange containing the values to replace, the replacing values and
     * instructions on how to apply the change
     */
    @SafeVarargs
    public final ReplaceAll<T> replaceAll(T... values) {
        return new ReplaceAll<>(clazz, values, this);
    }

    /**
     * Replaces the first element in an array which matches the given predicate with the specified replacing value.
     * The Predicate and the replacing value are stored in a new {@link ReplaceFirstIf} instance which only contains
     * a reference to the previous change
     * @param filter ({@code Predicate<T>}): predicate used to determine which element must be replaced
     * @param replacingValue ({@code T}): value used to replace element which matches the predicate
     * @return (ReplaceFirstIf\u003C T \u003E): new SingleThreadChange containing the predicate, the replacing value and
     * instructions on how to apply the change
     */
    public final ReplaceFirstIf<T> replaceFirst(final Predicate<T> filter, final T replacingValue) {
        return new ReplaceFirstIf<>(clazz, filter, replacingValue, this);
    }

    /**
     * Replaces the <strong>first</strong> occurrence of each specified elements by the associated values.
     * <list>
     *     <li>Parameters at even positions must be the values to replace</li>
     *     <li>Parameters at odd positions must be the replacing values</li>
     * </list>
     * <br><u><i>example</i></u><br>
     * <pre>{@code
     * replaceFirst(
     *      (T) value to replace, (T) replacing value,
     *      (T) value to replace, (T) replacing value,
     *      (T) value to replace, (T) replacing value
     * )
     * }</pre><br>
     * Values to replace and replacing values are stored in a new {@link ReplaceFirstOrLast} instance which only contains a
     * reference to the previous change
     * @param values ({@code T...}): value to replace - replacing values pairs
     * @return (ReplaceFirstOrLast\u003C T \u003E): new SingleThreadChange containing the values to replace, the replacing values and
     * instructions on how to apply the change
     */
    @SafeVarargs
    public final ReplaceFirstOrLast<T> replaceFirst(T... values) {
        return new ReplaceFirstOrLast<>(clazz, values, this);
    }

    /**
     * Replaces the last element in an array which matches the given predicate with the specified replacing value.
     * The Predicate and the replacing value are stored in a new {@link ReplaceLastIf} instance which only contains
     * a reference to the previous change
     * @param filter ({@code Predicate<T>}): predicate used to determine which element must be replaced
     * @param replacingValue ({@code T}): value used to replace element which matches the predicate
     * @return (ReplaceLastIf\u003C T \u003E): new SingleThreadChange containing the predicate, the replacing value and
     * instructions on how to apply the change
     */
    public final ReplaceLastIf<T> replaceLast(final Predicate<T> filter, final T replacingValue) {
        return new ReplaceLastIf<>(clazz, filter, replacingValue, this);
    }

    /**
     * Replaces the <strong>last</strong> occurrence of each specified elements by the associated values.
     * <list>
     *     <li>Parameters at even positions must be the values to replace</li>
     *     <li>Parameters at odd positions must be the replacing values</li>
     * </list>
     * <br><u><i>example</i></u><br>
     * <pre>{@code
     * replaceLast(
     *      (T) value to replace, (T) replacing value,
     *      (T) value to replace, (T) replacing value,
     *      (T) value to replace, (T) replacing value
     * )
     * }</pre><br>
     * Values to replace and replacing values are stored in a new {@link ReplaceFirstOrLast} instance which only contains a
     * reference to the previous change
     * @param values ({@code T...}): value to replace - replacing values pairs
     * @return (ReplaceFirstOrLast\u003C T \u003E): new SingleThreadChange containing the values to replace, the replacing values and
     * instructions on how to apply the change
     */
    @SafeVarargs
    public final ReplaceFirstOrLast<T> replaceLast(T... values) {
        return new ReplaceFirstOrLast<>(clazz, values, true, this);
    }

    // ====================================
    //              RETAINING
    // ====================================

    /**
     * Retains only the first instance of all the specified elements, if they exist in the array. The element to retain are stored
     * in a new {@link RetainFirst} instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): the elements to retain
     * @return (RetainFirst\u003C T \u003E): new SingleThreadChange containing the elements to retain and instructions on how to apply the change
     */
    @Override
    public final RetainFirst<T> retainFirst(Object... objects) {
        return new RetainFirst<>(clazz, objects, this);
    }

    /**
     * Retains only the first instance of all elements in the specified collection, if they exist in the array. The
     * element to retain are stored in a new {@link RetainFirst} instance which only contains a reference to the
     * previous change
     * @param c ({@code Collection<?> c}): collection containing the elements to retain
     * @return (RetainFirst\u003C T \u003E): new SingleThreadChange containing the elements to retain and instructions
     * on how to apply the change
     */
    @Override
    public RetainFirst<T> retainFirst(Collection<?> c) {
        return new RetainFirst<>(clazz, c.toArray(), this);
    }

    /**
     * Retains all the instances of all the specified elements, if they exist in the array. The elements to retain are stored
     * in a new {@link RetainAll} instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): the elements to retain
     * @return (RetainFirst\u003C T \u003E): new SingleThreadChange containing the elements to retain and instructions on how to apply the change
     */
    @Override
    public final RetainAll<T> retainAll(Object... objects) {
        return new RetainAll<>(clazz, objects, this);
    }

    /**
     * Retains all the instances of all the elements in the specified collection, if they exist in the array. The elements
     * to retain are stored in a new {@link RetainAll} instance which only contains a reference to the previous change
     * @param c ({@code Collection<?>}): collection of elements to retain
     * @return (RetainFirst\u003C T \u003E): new SingleThreadChange containing the elements to retain and instructions on how to apply the change
     */
    @Override
    public final RetainAll<T> retainAll(Collection<?> c) {
        return new RetainAll<>(clazz, c, this);
    }

    /**
     * Retains elements in an array only if they match the given predicate. The predicate is stored in a new {@link RetainIf}
     * instance which only contains a reference to the previous change
     * @param filter ({@code Predicate<? super T>}): predicate used to retain elements
     * @return (RetainIf\u003C T \u003E): new SingleThreadChange containing the predicate and instructions on how to apply the change
     */
    public final RetainIf<T> retainIf(Predicate<? super T> filter) {
        return new RetainIf<>(clazz, filter, this);
    }

    // ====================================
    //              CLEARING
    // ====================================

    /**
     * Clears an array, resulting in a zero-element array of the same type
     * @return (Clear\u003C T \u003E): new SingleThreadChange containing instructions on how to clear an array
     */
    @Override
    public final FunctionalChange<T> clear() {
        return new FunctionalChange<>(clazz, Functions.clear(), this);
    }

    // ====================================
    //              SORTING
    // ====================================

    /**
     * Sorts an array according to the default {@link com.company.utilities.comparators.ObjectComparator ObjectComparator}
     * @return (Ordered\u003C T \u003E): new SingleThreadChange containing instructions on how to sort an array
     */
    public final FunctionalChange<T> sorted() {
        return new FunctionalChange<>(clazz, Functions.sort(), this);
    }

    /**
     * Sorts an array according to the given {@link Comparator}
     * @param comparator ({@code Comparator<T>}): comparator used to sort the array
     * @return (Ordered\u003C T \u003E): new SingleThreadChange containing the comparator and instructions on how to sort an array
     */
    public final FunctionalChange<T> sorted(final Comparator<T> comparator) {
        return new FunctionalChange<>(clazz, Functions.sort(comparator), this);
    }

    // ====================================
    //               UNIQUE
    // ====================================

    /**
     * Retains only unique values in an array, according to the default
     * {@link com.company.utilities.comparators.ObjectComparator ObjectComparator}
     * @return (Unique\u003C T \u003E): new SingleThreadChange containing instructions on how to retain unique elements in the array
     */
    public final FunctionalChange<T> unique() {
        return new FunctionalChange<>(clazz, ArrayUtil::retainDistinct, this);
    }

    /**
     * Retains only unique values in an array, according to the give {@link Comparator}
     * @param comparator ({@code Comparator<T>}): comparator used to retain unique elements in the array
     * @return (Unique\u003C T \u003E): new SingleThreadChange containing the comparator and instructions on how to retain unique
     * elements in the array
     */
    public final FunctionalChange<T> unique(final Comparator<T> comparator) {
        return new FunctionalChange<>(clazz, array -> ArrayUtil.retainDistinct(array, comparator), this);
    }

    // ====================================
    //             FUNCTIONS
    // ====================================

    /**
     * Applies the given {@link Function} to each element in an array
     * @param function ({@code Function<T, T>}): function applied to every element in the array
     * @return (ForEach\u003C T \u003E): new SingleThreadChange containing the function and instructions on how to apply it to array elements
     */
    public final FunctionalChange<T> forEach(final Function<T, T> function) {
        return new FunctionalChange<>(clazz, Functions.forEach(function), this);
    }

    // ====================================
    //            INFORMATION
    // ====================================

    /**
     * Returns the indexes of the first occurrence of each element to find
     * @param toFind ({@code Object...}): the elements to find
     * @return (int[]): the indexes of first occurrence of each element
     */
    public final int[] findFirst(Object... toFind) {
        return (int[]) ChangeInformation.findFirst(toFind).getInformation(toArray());
    }

    /**
     * Returns all the indexes of each occurrence of every element to find
     * @param toFind ({@code Object...}): the elements to find
     * @return (int[]): the indexes of each occurrence of every element to find
     */
    public final int[] findAll(Object... toFind) {
        return (int[]) ChangeInformation.findAll(toFind).getInformation(toArray());
    }

    /**
     * Returns all the values at the specified indexes
     * @param indexes ({@code int...}): indexes at which to get the values
     * @return (T[]): the values at the specified indexes
     */
    public final T[] getAt(int... indexes) {
        return (T[]) ChangeInformation.getAt(indexes).getInformation(toArray());
    }

    /**
     * Returns the first values to match the given {@link Predicate}
     * @param filter ({@code Predicate<T>}): predicate used to check values
     * @return (T): the first value to match the given predicate
     */
    public final T getFirst(final Predicate<? super T> filter) {
        return (T) ChangeInformation.getFirst(filter).getInformation(toArray());
    }

    /**
     * Returns all values to match the given {@link Predicate}
     * @param filter ({@code Predicate<T>}): predicate used to check values
     * @return (T[]): all value that match the given predicate
     */
    public final T[] getAll(final Predicate<T> filter) {
        return (T[]) ChangeInformation.getAll(filter).getInformation(toArray());
    }

    /**
     * Counts the number of occurrences of each value
     * @param toFind ({@code Object...}): the values to find
     * @return (int[]): the number of occurrence of each value
     */
    public int[] countMatches(final Object... toFind) {
        return (int[]) ChangeInformation.count(toFind).getInformation(toArray());
    }

    /**
     * Counts the number of values that match the given {@link Predicate}
     * @param filter ({@code Predicate<T>}): predicate used to check values
     * @return (int): the number of values which match the given predicate
     */
    public int countMatches(final Predicate<? super T> filter) {
        return (int) ChangeInformation.count(filter).getInformation(toArray());
    }

    /**
     * Returns the sum of each element in an array
     * @return (int): the sum of every element in the array
     */
    public T sumOf() throws OperationNotSupportedException {
        return (T) ChangeInformation.sum().getInformation(toArray());
    }

    public T sumOf(
            @NotNull final Operator<T> operator
    ) throws OperationNotSupportedException {
        Objects.requireNonNull(operator);
        return (T) ChangeInformation.sum(operator).getInformation(toArray());
    }

    /**
     * Returns the difference of every element in an array
     * @return (int): the difference of every element in the array
     */
    public T differenceOf() throws OperationNotSupportedException {
        return (T) ChangeInformation.difference().getInformation(toArray());
    }

    public T differenceOf(
            @NotNull final Operator<T> operator
    ) throws OperationNotSupportedException {
        Objects.requireNonNull(operator);
        return (T) ChangeInformation.difference(operator).getInformation(toArray());
    }

    public T multiplyAll() throws OperationNotSupportedException {
        return (T) ChangeInformation.multiply().getInformation(toArray());
    }

    public T multiplyAll(
            @NotNull final Operator<T> operator
    ) throws OperationNotSupportedException {
        Objects.requireNonNull(operator);
        return (T) ChangeInformation.multiply(operator).getInformation(toArray());
    }

    public T divideAll() throws OperationNotSupportedException {
        return (T) ChangeInformation.divide().getInformation(toArray());
    }

    public T divideAll(
            @NotNull final Operator<T> operator
    ) throws OperationNotSupportedException {
        Objects.requireNonNull(operator);
        return (T) ChangeInformation.divide(operator).getInformation(toArray());
    }

    public T getInformation(
            @NotNull final ChangeInformation<T> information
    ) {
        Objects.requireNonNull(information);
        return (T) information.getInformation(toArray());
    }
}
