package com.company.collections.changeAPI.changes.singlethread;

import com.company.collections.changeAPI.Change;
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
import com.company.collections.changeAPI.generation.Generator;
import com.company.collections.changeAPI.information.ChangeInformation;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SingleThreadChange<E> extends Change<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public SingleThreadChange(
            final Class<E> clazz
    ) {
        super(clazz);
    }

    public SingleThreadChange(
            final Class<E> clazz,
            final Change<E> parent
    ) {
        super(clazz, parent);
    }

    public SingleThreadChange(
            final Class<E> clazz,
            final Change<E> parent,
            final E[] array
    ) {
        super(clazz, parent, array);
    }

    // ====================================
    //               ADDING
    // ====================================

    /**
     * Adds the specified element to the {@link SingleThreadChange}. New elements are stored in a new {@link Add} instance which
     * <strong>only</strong> contains these elements and a reference to the previous SingleThreadChange
     * @param e ({@code E}): the element to add
     * @return (Add\u003C E \u003E): new SingleThreadChange containing the new element and instructions on how to add it
     */
    @Override
    public final Add<E> add(E e) {
        return new Add<>(clazz, (E[]) new Object[]{e}, this);
    }

    /**
     * Adds all the specified elements to the {@link SingleThreadChange}. New elements are stored in a new {@link Add} instance
     * which <strong>only</strong> contains these elements and a reference to the previous SingleThreadChange
     * @param elements ({@code E...}): elements to add
     * @return (Add\u003C E \u003E): new SingleThreadChange containing the new elements and instructions on how to add them
     */
    @SafeVarargs
    public final Add<E> addAll(E... elements) {
        return new Add<>(clazz, elements, this);
    }

    /**
     * Adds all the elements in the specified collection to the {@link SingleThreadChange}. New elements are stored in a new
     * {@link Add} instance which <strong>only</strong> contains these elements and a reference to the previous SingleThreadChange
     * @param c ({@code Collection<? extends E>}): collection of elements to add
     * @return (Add\u003C E \u003E): new SingleThreadChange containing the new elements and instructions on how to add them
     */
    @Override
    public final Add<E> addAll(Collection<? extends E> c) {
        return new Add<>(clazz, c, this);
    }

    public final Add<E> addAll(final Generator<E> generator, final int length) {
        return new Add<>(clazz, generator.generateArray(clazz, length), this);
    }

    // ====================================
    //              REMOVING
    // ====================================

    /**
     * Removes only the first instance of all the specified objects. The objects to remove are stored in a new {@link RemoveFirst}
     * instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): objects to remove
     * @return (RemoveFirst\u003C E \u003E): new SingleThreadChange containing all the elements to remove and instructions on how to remove them
     */
    public final RemoveFirst<E> removeFirst(Object... objects) {
        return new RemoveFirst<>(clazz, objects, this);
    }

    public final RemoveFirst<E> removeFirst(final int length, final Generator<E> generator) {
        return new RemoveFirst<>(clazz, generator.generateArray(clazz, length), this);
    }

    /**
     * Removes all the instances of all the specified objects. The objects to remove are stored in a new {@link RemoveAll}
     * instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): objects to remove
     * @return (RemoveFirst\u003C E \u003E): new SingleThreadChange containing all the elements to remove and instructions on how to remove them
     */
    public final RemoveAll<E> removeAll(Object... objects) {
        return new RemoveAll<>(clazz, objects, this);
    }

    /**
     * Removes all the instances of all the elements in the collection. The elements to remove are stored in a new {@link RemoveAll}
     * instance which only contains a reference to the previous change
     * @param c ({@code Collection<?> c}): collection containing the elements to remove
     * @return (RemoveFirst\u003C E \u003E): new SingleThreadChange containing all the elements to remove and instructions on how to remove them
     */
    @Override
    public final RemoveAll<E> removeAll(Collection<?> c) {
        return new RemoveAll<>(clazz, c, this);
    }

    public final RemoveAll<E> removeAll(final int length, final Generator<?> generator) {
        return new RemoveAll<>(clazz, ((Generator<Object>) generator).generateArray(Object.class, length) , this);
    }

    /**
     * Removes elements in the change if they match the given {@link Predicate}. The predicate is stored in a new {@link RemoveIf}
     * instance which only contains a reference to the previous change
     * @param filter ({@code Predicate<? super E>}): predicate used to filter out elements
     * @return (RemoveIf\u003C E \u003E): new SingleThreadChange containing the predicate used to remove elements and instructions on how to remove them
     */
    @Override
    public final RemoveIf<E> removeIf(Predicate<? super E> filter) {
        return new RemoveIf<>(clazz, filter, this);
    }

    /**
     * Removes elements on the change at the specified indexes. The indexes are stored in a new {@link RemoveAt} instance
     * which only contains a reference to the previous change
     * @param indexes ({@code int...}): indexes at which to remove the elements
     * @return (RemoveAt\u003C E \u003E): new change containing the indexes at which to remove elements and instructions on how to remove them
     */
    public final RemoveAt<E> removeAt(int... indexes) {
        return new RemoveAt<>(clazz, indexes, this);
    }

    public final RemoveAt<E> removeAt(final int length, final Generator<Integer> generator) {
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
     *      (int) index, (E) value,
     *      (int) index, (E) value,
     *      (int) index, (E) value
     * )
     * }</pre><br>
     * Indexes and elements to replace are stored in a new {@link ReplaceAt} instance which only contains a reference
     * to the previous change
     * @param objects ({@code Object...}): index-element pairs
     * @return (ReplaceAt\u003C E \u003E): new SingleThreadChange containing the indexes at which to replace values, the replacing
     * values and instructions on how to apply the change
     */
    public final ReplaceAt<E> replaceAt(Object... objects) {
        return new ReplaceAt<>(clazz, objects, this);
    }

    public final ReplaceAt<E> setAt(final int @NotNull [] indexes, final E @NotNull [] replacing) {
        return new ReplaceAt<>(clazz, indexes, replacing, this);
    }

    public final ReplaceAt<E> setAt(final int @NotNull [] indexes, final @NotNull E value) {
        final E[] replacing = (E[]) Array.newInstance(clazz, indexes.length);
        Arrays.fill(replacing, value);
        return new ReplaceAt<>(clazz, indexes, replacing, this);
    }

    /**
     * Replaces all elements in an array which match the given predicate with the specified replacing value. The Predicate
     * and the replacing value are stored in a new {@link ReplaceAllIf} instance which only contains a reference
     * to the previous change
     * @param filter ({@code Predicate<E>}): predicate used to determine which elements must be replaced
     * @param replacingValue ({@code E}): value used to replace elements which match the predicate
     * @return (ReplaceAllIf \ u003C E \ u003E): new SingleThreadChange containing the predicate, the replacing values and
     * instructions on how to apply the change
     */
    public final ReplaceAllIf<E> replaceAll(final Predicate<E> filter, final E replacingValue) {
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
     *      (E) value to replace, (E) replacing value,
     *      (E) value to replace, (E) replacing value,
     *      (E) value to replace, (E) replacing value
     * )
     * }</pre><br>
     * Values to replace and replacing values are stored in a new {@link ReplaceAll} instance which only contains a
     * reference to the previous change
     * @param values ({@code E...}): value to replace - replacing values pairs
     * @return (ReplaceAt\u003C E \u003E): new SingleThreadChange containing the values to replace, the replacing values and
     * instructions on how to apply the change
     */
    @SafeVarargs
    public final ReplaceAll<E> replaceAll(E... values) {
        return new ReplaceAll<>(clazz, values, this);
    }

    /**
     * Replaces the first element in an array which matches the given predicate with the specified replacing value.
     * The Predicate and the replacing value are stored in a new {@link ReplaceFirstIf} instance which only contains
     * a reference to the previous change
     * @param filter ({@code Predicate<E>}): predicate used to determine which element must be replaced
     * @param replacingValue ({@code E}): value used to replace element which matches the predicate
     * @return (ReplaceFirstIf\u003C E \u003E): new SingleThreadChange containing the predicate, the replacing value and
     * instructions on how to apply the change
     */
    public final ReplaceFirstIf<E> replaceFirst(final Predicate<E> filter, final E replacingValue) {
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
     *      (E) value to replace, (E) replacing value,
     *      (E) value to replace, (E) replacing value,
     *      (E) value to replace, (E) replacing value
     * )
     * }</pre><br>
     * Values to replace and replacing values are stored in a new {@link ReplaceFirstOrLast} instance which only contains a
     * reference to the previous change
     * @param values ({@code E...}): value to replace - replacing values pairs
     * @return (ReplaceFirstOrLast\u003C E \u003E): new SingleThreadChange containing the values to replace, the replacing values and
     * instructions on how to apply the change
     */
    @SafeVarargs
    public final ReplaceFirstOrLast<E> replaceFirst(E... values) {
        return new ReplaceFirstOrLast<>(clazz, values, this);
    }

    /**
     * Replaces the last element in an array which matches the given predicate with the specified replacing value.
     * The Predicate and the replacing value are stored in a new {@link ReplaceLastIf} instance which only contains
     * a reference to the previous change
     * @param filter ({@code Predicate<E>}): predicate used to determine which element must be replaced
     * @param replacingValue ({@code E}): value used to replace element which matches the predicate
     * @return (ReplaceLastIf\u003C E \u003E): new SingleThreadChange containing the predicate, the replacing value and
     * instructions on how to apply the change
     */
    public final ReplaceLastIf<E> replaceLast(final Predicate<E> filter, final E replacingValue) {
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
     *      (E) value to replace, (E) replacing value,
     *      (E) value to replace, (E) replacing value,
     *      (E) value to replace, (E) replacing value
     * )
     * }</pre><br>
     * Values to replace and replacing values are stored in a new {@link ReplaceFirstOrLast} instance which only contains a
     * reference to the previous change
     * @param values ({@code E...}): value to replace - replacing values pairs
     * @return (ReplaceFirstOrLast\u003C E \u003E): new SingleThreadChange containing the values to replace, the replacing values and
     * instructions on how to apply the change
     */
    @SafeVarargs
    public final ReplaceFirstOrLast<E> replaceLast(E... values) {
        return new ReplaceFirstOrLast<>(clazz, values, true, this);
    }

    // ====================================
    //              RETAINING
    // ====================================

    /**
     * Retains only the first instance of all the specified elements, if they exist in the array. The element to retain are stored
     * in a new {@link RetainFirst} instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): the elements to retain
     * @return (RetainFirst\u003C E \u003E): new SingleThreadChange containing the elements to retain and instructions on how to apply the change
     */
    @Override
    public final RetainFirst<E> retainFirst(Object... objects) {
        return new RetainFirst<>(clazz, objects, this);
    }

    /**
     * Retains only the first instance of all elements in the specified collection, if they exist in the array. The
     * element to retain are stored in a new {@link RetainFirst} instance which only contains a reference to the
     * previous change
     * @param c ({@code Collection<?> c}): collection containing the elements to retain
     * @return (RetainFirst\u003C E \u003E): new SingleThreadChange containing the elements to retain and instructions
     * on how to apply the change
     */
    @Override
    public RetainFirst<E> retainFirst(Collection<?> c) {
        return new RetainFirst<>(clazz, c.toArray(), this);
    }

    /**
     * Retains all the instances of all the specified elements, if they exist in the array. The elements to retain are stored
     * in a new {@link RetainAll} instance which only contains a reference to the previous change
     * @param objects ({@code Object...}): the elements to retain
     * @return (RetainFirst\u003C E \u003E): new SingleThreadChange containing the elements to retain and instructions on how to apply the change
     */
    @Override
    public final RetainAll<E> retainAll(Object... objects) {
        return new RetainAll<>(clazz, objects, this);
    }

    /**
     * Retains all the instances of all the elements in the specified collection, if they exist in the array. The elements
     * to retain are stored in a new {@link RetainAll} instance which only contains a reference to the previous change
     * @param c ({@code Collection<?>}): collection of elements to retain
     * @return (RetainFirst\u003C E \u003E): new SingleThreadChange containing the elements to retain and instructions on how to apply the change
     */
    @Override
    public final RetainAll<E> retainAll(Collection<?> c) {
        return new RetainAll<>(clazz, c, this);
    }

    /**
     * Retains elements in an array only if they match the given predicate. The predicate is stored in a new {@link RetainIf}
     * instance which only contains a reference to the previous change
     * @param filter ({@code Predicate<? super E>}): predicate used to retain elements
     * @return (RetainIf\u003C E \u003E): new SingleThreadChange containing the predicate and instructions on how to apply the change
     */
    public final RetainIf<E> retainIf(Predicate<? super E> filter) {
        return new RetainIf<>(clazz, filter, this);
    }

    // ====================================
    //              CLEARING
    // ====================================

    /**
     * Clears an array, resulting in a zero-element array of the same type
     * @return (Clear\u003C E \u003E): new SingleThreadChange containing instructions on how to clear an array
     */
    @Override
    public final FunctionalChange<E> clear() {
        return new FunctionalChange<>(clazz, Functions.clear(), this);
    }

    // ====================================
    //              SORTING
    // ====================================

    /**
     * Sorts an array according to the default {@link com.company.utilities.comparators.ObjectComparator ObjectComparator}
     * @return (Ordered\u003C E \u003E): new SingleThreadChange containing instructions on how to sort an array
     */
    public final FunctionalChange<E> sorted() {
        return new FunctionalChange<>(clazz, Functions.sort(), this);
    }

    /**
     * Sorts an array according to the given {@link Comparator}
     * @param comparator ({@code Comparator<E>}): comparator used to sort the array
     * @return (Ordered\u003C E \u003E): new SingleThreadChange containing the comparator and instructions on how to sort an array
     */
    public final FunctionalChange<E> sorted(final Comparator<E> comparator) {
        return new FunctionalChange<>(clazz, Functions.sort(comparator), this);
    }

    // ====================================
    //               UNIQUE
    // ====================================

    /**
     * Retains only unique values in an array, according to the default
     * {@link com.company.utilities.comparators.ObjectComparator ObjectComparator}
     * @return (Unique\u003C E \u003E): new SingleThreadChange containing instructions on how to retain unique elements in the array
     */
    public final FunctionalChange<E> unique() {
        return new FunctionalChange<>(clazz, ArrayUtil::retainDistinct, this);
    }

    /**
     * Retains only unique values in an array, according to the give {@link Comparator}
     * @param comparator ({@code Comparator<E>}): comparator used to retain unique elements in the array
     * @return (Unique\u003C E \u003E): new SingleThreadChange containing the comparator and instructions on how to retain unique
     * elements in the array
     */
    public final FunctionalChange<E> unique(final Comparator<E> comparator) {
        return new FunctionalChange<>(clazz, array -> ArrayUtil.retainDistinct(array, comparator), this);
    }

    // ====================================
    //             FUNCTIONS
    // ====================================

    /**
     * Applies the given {@link Function} to each element in an array
     * @param function ({@code Function<E, E>}): function applied to every element in the array
     * @return (ForEach\u003C E \u003E): new SingleThreadChange containing the function and instructions on how to apply it to array elements
     */
    public final FunctionalChange<E> forEach(final Function<E, E> function) {
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
     * @return (E[]): the values at the specified indexes
     */
    public final E[] getAt(int... indexes) {
        return (E[]) ChangeInformation.getAt(indexes).getInformation(toArray());
    }

    /**
     * Returns the first values to match the given {@link Predicate}
     * @param filter ({@code Predicate<E>}): predicate used to check values
     * @return (E): the first value to match the given predicate
     */
    public final E getFirst(final Predicate<? super E> filter) {
        return (E) ChangeInformation.getFirst(filter).getInformation(toArray());
    }

    /**
     * Returns all values to match the given {@link Predicate}
     * @param filter ({@code Predicate<E>}): predicate used to check values
     * @return (E[]): all value that match the given predicate
     */
    public final E[] getAll(final Predicate<E> filter) {
        return (E[]) ChangeInformation.getAll(filter).getInformation(toArray());
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
     * @param filter ({@code Predicate<E>}): predicate used to check values
     * @return (int): the number of values which match the given predicate
     */
    public int countMatches(final Predicate<? super E> filter) {
        return (int) ChangeInformation.count(filter).getInformation(toArray());
    }

    /**
     * Returns the sum of each element in an array
     * @return (int): the sum of every element in the array
     */
    public int sumOf() {
        return (int) ChangeInformation.sum().getInformation(toArray());
    }

    /**
     * Returns the difference of every element in an array
     * @return (int): the difference of every element in the array
     */
    public int differenceOf() {
        return -sumOf();
    }
}
