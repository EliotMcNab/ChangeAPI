package com.company.collections.changeAPI;

import com.company.collections.ImmutableCollection;
import com.company.collections.changeAPI.changes.parallel.remove.ParallelRemoveAll;
import com.company.collections.changeAPI.changes.parallel.remove.ParallelRemoveAt;
import com.company.collections.changeAPI.changes.parallel.remove.ParallelRemoveFirst;
import com.company.collections.changeAPI.changes.parallel.replace.ParallelReplaceAll;
import com.company.collections.changeAPI.changes.parallel.replace.ParallelReplaceAllIf;
import com.company.collections.changeAPI.changes.parallel.replace.ParallelReplaceFirstOrLast;
import com.company.collections.changeAPI.changes.singlethread.Origin;
import com.company.collections.changeAPI.changes.singlethread.add.Add;
import com.company.collections.changeAPI.changes.singlethread.add.SequentialAdd;
import com.company.collections.changeAPI.changes.singlethread.functions.FunctionalChange;
import com.company.collections.changeAPI.changes.singlethread.remove.*;
import com.company.collections.changeAPI.changes.singlethread.replace.*;
import com.company.collections.changeAPI.changes.singlethread.retain.RetainAll;
import com.company.collections.changeAPI.changes.singlethread.retain.RetainFirst;
import com.company.collections.changeAPI.changes.singlethread.retain.RetainIf;
import com.company.collections.changeAPI.errors.InaccessibleValueException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;

// TODO: make this actually immutable by implementing deep array copy
/**
 * An immutable linked collection for efficiently manipulating arrays. Changes are not applied an array, instead they
 * store the modifications to be applied to an array at a later time. To avoid having to copy over data and the
 * performance impact associated to it, each Change only stores the information relevant to a specific
 * modification such as removing, adding, replacing... Previous changes are accessed through references, with each
 * Change having a reference to its parent and an integer generation, with generation 0 corresponding to the
 * original Change.<br><br>
 *
 * For increased performance, Changes are only applied when calling the toArray or applyTo method. Changes are either
 * applied directly if of generation 0 or go through resolution otherwise.<br><br>
 *
 * <u><i>Resolution:</i></u><br><br>
 *
 * <i>Resolution</i> handles recovering successive Changes and grouping similar Changes together for increased
 * performance. Change subclasses specify on an individual level with which other classes they can be grouped with -or
 * sequentialised-, and the means by which to sequentialise them. Sequentialisation is handled by separate classes such
 * as {@link SequentialAdd SequentialAdd} or {@link SequentialReplaceAt}, and group together Changes for which a single
 * optimised algorithm can be applied rather than having to resort to multiple iterations over the same array.<br><br>
 *
 * <u><i>Batch Changes:</i></u><br><br>
 *
 * The Change API also allow for multiple changes to be applied at once within the same method, which makes it more
 * efficient and readable to apply them rather than having to rely on multiple successive function calls. This is the
 * case for example for replacing multiple elements at several indexes, or replacing multiple values with multiple
 * different other values at once.<br><br>
 *
 * <u>ex</u><br>
 * <pre>{@code
 * replaceAll(
 *      (E) value to replace, (E) replacing value,
 *      (E) value to replace, (E) replacing value,
 *      (E) value to replace, (E) replacing value
 * )
 * }</pre><br>
 *
 * <u><i>Operation Control</i></u><br><br>
 *
 * Most operations in the Change API can be applied with various control over which elements to target. These include
 * mainly:
 * <ul>
 *     <li>Targeting the first occurrence of an element</li>
 *     <li>Targeting the last occurrence of an element</li>
 *     <li>Targeting all occurrences of an element</li>
 *     <li>Targeting an element at a specific position</li>
 *     <li>Conditionally targeting an element through the use of a {@link Predicate}</li>
 * </ul><br>
 *
 * <u><i>Functional Style</i></u><br><br>
 *
 * Changes are implemented with a functional style in mind. While it is possible to use the individual classes behind
 * the Change API such as {@link Add}, {@link RemoveAll} or {@link RetainFirst}, the methods available in the Change
 * class allow for easy chaining of operations while being able to revert to a previous state thanks to Changes
 * only storing modifications and not mutating previous Changes<br><br>
 *
 * <u>ex</u><br>
 * <pre>{@code
 * Change.of(Integer.class).addAll(1, 7, 9, 5, 0, 2, 6, 8, 16, 9, 0)
 *                         .forEach(integer -> integer + 1)
 *                         .retainIf(integer -> integer % 2 == 0)
 *                         .unique()
 *                         .sumOf()
 * }</pre>
 *
 * @param <E> the type the Change operates on
 * @author Eliot McNab
 * @see Add
 * @see FunctionalChange
 * @see RemoveAll
 * @see RemoveAt
 * @see RemoveFirst
 * @see RemoveIf
 * @see ReplaceAll
 * @see ReplaceAllIf
 * @see ReplaceAt
 * @see ReplaceFirstIf
 * @see ReplaceLastIf
 * @see ReplaceFirstOrLast
 * @see RetainAll
 * @see RetainFirst
 * @see RetainIf
 * @see ParallelRemoveAll
 * @see ParallelRemoveAt
 * @see ParallelRemoveFirst
 * @see ParallelReplaceAll
 * @see ParallelReplaceAllIf
 * @see ParallelReplaceFirstOrLast
 * @see ParallelAdapter
 */
public abstract class Change<E> implements ImmutableCollection<E>, Iterable<Change<E>> {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Class<E> clazz;
    private final Change<E> parent;
    protected final E[] array;
    private final int generation;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public Change(
            final Class<E> clazz
    ) {
        this.clazz = clazz;
        this.parent = null;
        this.array = null;
        this.generation = 0;
    }

    public Change(
            final Class<E> clazz,
            final Change<E> parent
    ) {
        this.clazz = clazz;
        this.parent = parent;
        this.array = parent == null ? null : parent.array;
        this.generation = parent == null ? 0 : parent.generation + 1;
    }

    public Change(
            final Class<E> clazz,
            final Change<E> parent,
            final E[] array
    ) {
        this.clazz = clazz;
        this.parent = parent;
        this.array = array;
        this.generation = parent == null ? 0 : parent.generation + 1;
    }

    /**
     * Creates a new {@link Change} with the specified elements as a base, elements are collected into an {@link Origin}
     * @param elements ({@code E...}): elements making up the Origin of the Change
     * @return (Origin\u003C E \u003E): Origin containing the initial elements
     * @param <E> type of the elements
     */
    @SafeVarargs
    public static <E> Origin<E> of(final E... elements) {
        return new Origin<>((Class<E>) elements.getClass().componentType(), elements);
    }

    /**
     * Creates a new empty {@link Change} of the specified class
     * @param clazz ({@code Class<E>}): the class of the Change
     * @return (Origin\u003C E \u003E): {@link Origin} containing an empty array of the specified class
     * @param <E> the type of the Change
     */
    public static <E> Origin<E> of(final Class<E> clazz) {
        return new Origin<>(clazz, (E[]) Array.newInstance(clazz, 0));
    }

    // ====================================
    //             ITERATION
    // ====================================

    /**
     * Iterates over all linked {@link Change Changes} up to generation 0
     * @return (Iterator\u003C Change \u003C E \u003E \u003E): iterator over all Changes up to generation 0
     */
    @NotNull
    @Override
    public Iterator<Change<E>> iterator() {
        return new Itr(this);
    }

    private class Itr implements Iterator<Change<E>> {

        private final Change<E>[] changes;
        private int cursor = 0;

        public Itr(
                final Change<E> change
        ) {
            this.changes = retrieveAllChanges(change);
        }

        @Override
        public boolean hasNext() {
            return cursor < changes.length;
        }

        @Override
        public Change<E> next() {
            return changes[cursor++];
        }
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    /**
     * Gets the {@link Change}'s current generation
     * @return (int): the change's generation
     */
    public final int getGeneration() {
        return generation;
    }

    public final Change<E> getPreviousChange() {
        return parent;
    }

    // ====================================
    //             CONTENTS
    // ====================================

    /**
     * <i>If an array has been associated to this {@link Change}</i> using {@code Change.of}, returns the size of that
     * array once all change have been applied to it. If the current Change has no array associated to it, returns a
     * length of -1 instead<br><br>
     *
     * <u><i>Warning:</i></u><br><br>
     *
     * This method has an <i>implicit call to toArray</i> which might result in poor performance when called repeatedly
     * on large Changes. To avoid this issue, used the {@code optimise} method and call {@code size} on the resulting
     * {@link Origin} instead.<br><br>
     *
     * @return (int): the size of the array associated to this Change, -1 if no array is associated
     */
    @Override
    public final int size() {
        if (array == null) {
            return -1;
        } else {
            return toArray().length;
        }
    }

    /**
     * Determines whether this {@link Change}'s array will be empty after all changes have been applied to it. Also
     * returns true if this Change has no array associated to it.<br<br>
     *
     * <u><i>Warning:</i></u><br><br>
     *
     * This method has an <i>implicit call to toArray</i> which might result in poor performance when called repeatedly
     * on large Changes. To avoid this issue, used the {@code optimise} method and call {@code isEmpty} on the resulting
     * {@link Origin} instead.<br><br>
     *
     * @return (boolean): whether this Change's array will be empty after calling toArray
     */
    @Override
    public final boolean isEmpty() {
        return size() <= 0;
    }

    /**
     * Determines whether this {@link Change}'s array will contain the given element once all changes have been applied
     * to it.<br><br>
     *
     * <u><i>Warning:</i></u><br><br>
     *
     * This method has an <i>implicit call to toArray</i> which might result in poor performance when called repeatedly
     * on large Changes. To avoid this issue, used the {@code optimise} method and call {@code contains} on the
     * resulting {@link Origin} instead.<br><br>
     *
     * @param o ({@code Object}): the object to check the occurrence of
     * @return (boolean): whether the array resulting of applying this Change will contain the given object
     */
    @Override
    public final boolean contains(Object o) {
        if (array == null) {
            return false;
        } else {
            return Arrays.asList(toArray()).contains(o);
        }
    }

    /**
     * Determines whether this {@link Change}'s array will contain all the elements in the given {@link Collection}
     * once all changes have been applied to it.<br><br>
     *
     * <u><i>Warning:</i></u><br><br>
     *
     * This method has an <i>implicit call to toArray</i> which might result in poor performance when called repeatedly
     * on large Changes. To avoid this issue, used the {@code optimise} method and call {@code containsAll} on the
     * resulting {@link Origin} instead.<br><br>
     *
     * @param c ({@code Collection<?>}): the collection to check for
     * @return (boolean): whether the array resulting of applying this Change will contain the given object
     */
    @Override
    public final boolean containsAll(Collection<?> c) {
        if (array == null) {
            return false;
        } else {
            return new HashSet<>(Arrays.asList(toArray())).containsAll(c);
        }
    }

    // ====================================
    //              APPLYING
    // ====================================

    /**
     * <strong>Must be overridden in child classes</strong>. Used to specify with which {@link Change} a subclass can be
     * sequentialised, resulting in a call to {@code toSequential}.<br><br>
     *
     * <i>Sequentialisable changes</i> are similar changes which can be grouped into a single instance and be applied
     * together without affecting their individual outcome. For performance reasons, when a change is applied to an
     * array using the {@code toArray} or {@code applyTo} methods, sequentialisable changes are grouped together and
     * applied at the same time rather than individually.<br><br>
     *
     * <u><i>example:</i></u><br><br>
     * - {@link RemoveFirst} and {@link RemoveAll} are sequentialisable changes since in is possible to regroup the
     * deletion of <i>all instances</i> of an element and the deletion of <i>only the first instance</i> of an element
     * into a single algorithm, thus avoiding having to loop over an array twice.<br><br>
     *
     * <u><i>implementation:</i></u><br><br>
     * - Implementation is typically handled by a <i>separate class</i> which takes multiple sequentialisable changes as arguments
     * and handles applying them optimally. This class should be returned by the {@code toSequential} method which
     * must be overridden in child classes. In the case of {@link RemoveFirst} and {@link RemoveAll}, the class
     * {@link com.company.collections.changeAPI.changes.singlethread.remove.SequentialRemove SequentialRemove} handles sequentialisation<br>
     * - In the case where a class <i>cannot be sequentialised</i>, {@code canSequentialise} should return false, which
     * will result in {@code toSequential} never being called<br><br>
     *
     * @param change ({@code Change<E>}): the change we would like to check for sequentialisation
     * @return (boolean): whether the given change can be sequentialised with the class instance on which this method
     * is called
     * @see SequentialAdd
     * @see SequentialRemove
     * @see SequentialRemoveAt
     * @see SequentialRemoveIf
     * @see SequentialReplaceAt
     * @see SequentialReplaceFirstIf
     * @see SequentialReplaceLastIf
     * @see SequentialReplaceAllIf
     */
    protected abstract boolean canSequentialise(final Change<E> change);

    /**
     * <strong>Must be overridden in child classes</strong>. Used to return a sequentialised {@link Change} which handles
     * applying the given changes simultaneously for better performance<br><br>
     *
     * <i>Sequentialisable changes</i> are similar changes which can be grouped into a single instance and be applied
     * together without affecting their individual outcome. For performance reasons, when a change is applied to an
     * array using the {@code toArray} or {@code applyTo} methods, sequentialisable changes are grouped together and
     * applied at the same time rather than individually.<br><br>
     *
     * A <i>sequentialised change</i> is a change which regroups multiple sequentialisable changes and handles applying
     * them simultaneously. <br><br>
     * @param changes (@code Change<E>[]): the changes to sequentialise
     * @return (Change\u003C E \u003E): resulting sequentialised change
     * @see com.company.collections.changeAPI.changes.singlethread.remove.SequentialRemove SequentialRemove
     */
    protected abstract Change<E> toSequential(Change<E>[] changes);

    /**
     * Combines all linked {@link Change Changes} up to generation 0 (the original change) into one single generation 0
     * {@link Origin}. This is especially useful when:
     * <list>
     *     <li>Wanting to save the state of a change before applying further modifications</li>
     *     <li>Having to call multiple methods such as {@code getAll} or {@code findFirst} which result in a call
     *     to {@code toArray}, and would otherwise hamper performance when called multiple times on a change of
     *     generation superior to 0</li>
     * </list><br>
     * <u><i>Warning:</i></u><br><br>
     * - Requires an array to be linked to the current {@link Change}, which is only the case if the change was created by calling
     * {@code Change.of} or if it is the result of modifying a change {@link Origin}.
     *
     * @return (Origin\u003C E \u003E): resulting combined change
     */
    public final Origin<E> optimise() {
        return new Origin<>(clazz, toArray());
    }

    /**
     * Applies the {@link Change} instance from which this method is being called to the given array. Should be used
     * when wanting to apply the same change to <i>multiple arrays</i> or when a change was not created by using<br><br>
     *
     * If the Change is of generation 0, then it is applied directly. Otherwise, Changes undergo <i>resolution</i>
     * during which sequential changes are sequentialised for better performance and applied together. <br><br>
     * {@code Change.of} and has no base array associated to it, which would result in an error when calling toArray.
     * @param array ({@code E[]}): the array to which to apply the change
     * @return (E[]): resulting changed array
     */
    public final E[] applyTo(@NotNull E[] array) {
        Objects.requireNonNull(array);

        if (getGeneration() == 0) return applyToImpl(array);
        else                      return resolve(array);
    }

    /**
     * Applies the {@link Change} to the given {@link Collection} through a call to its toArray method. Should be used
     * when wanting to apply the same change to <i>multiple arrays</i> or when a change was not created by using<br><br>
     *
     * If the Change is of generation 0, then it is applied directly. Otherwise, Changes undergo <i>resolution</i>
     * during which sequential changes are sequentialised for better performance and applied together. <br><br>
     *
     * {@code Change.of} and has no base array associated to it, which would result in an error when calling toArray.
     * @param c ({@code Collection<? extends E>}): the array to which to apply the change
     * @return (E[]): resulting changed array
     */
    public final E[] applyTo(Collection<? extends E> c) {
        return applyTo((E[]) c.toArray());
    }

    /**
     * <strong>Must be overridden in child classes</strong>. Specifies how a {@link Change} should be applied to a given
     * array <strong>without mutating it</strong>. Instead, child classes should always rely on array copying instead of
     * directly manipulating the original array.<br><br>
     *
     * @implNote this is likely to change in later versions, with applyTo providing Changes with a deep copy of the
     * original array instead of the array itself
     * @param array ({@code E[]}): the array to apply the change to
     * @return (E[]): resulting array with the change applied
     */
    protected abstract E[] applyToImpl(@NotNull E[] array);

    /**
     * <i>Resolution</i> occurs when the {@link Change} being applied to an array is of a generation greater than 0
     * (ie: there were other changes before it). It that case it is necessary to retrieve all changes <i>before</i> the
     * current change and apply them in the correct order.<br><br>
     *
     * <u><i>Sequentialisation:</i></u><br><br>
     *
     * Resolution is when changes are sequentialised. Instead of applying changes one by one, resolution looks for
     * consecutive changes which can be grouped together. The resulting sequentialised changes are then applied as one
     * to the array. If a change cannot be sequentialised, it is applied directly. Changes specify whether they can be
     * sequentialised through the canSequentialise method<br><br>
     *
     * <u><i>Warning:</i></u><br><br>
     *
     * Sequentialisable changes <i>cannot have toSequential return null</i>, otherwise change resolution will fail due
     * to a NullPointerException<br><br>
     *
     * @param array ({@code E[]}): the array to apply the Change to
     * @return (E[]): resulting changed array
     */
    protected final E[] resolve(E[] array) {

        final Change<E>[] allChanges = retrieveAllChanges(this);

        // region applying changes
        E[] result = array;

        int i = 0; // index in allChanges
        int lastIndex = 0; // last value of 'i' before looking for sequential changes

        // for every change...
        for (; i < allChanges.length; i++) {

            // ...looks for sequentialisable changes
            for (; i+1 < allChanges.length && allChanges[i].canSequentialise(allChanges[i+1]); i++) {}

            final Change<E> currentChange;            // the next change to be applied to the result array
            final int sequentialLength = i - lastIndex; // number of changes which can be sequentialised

            // if multiple changes can be sequentialised...
            if (sequentialLength > 1) {
                // ...extracts them from the change array...
                final Change<E>[] subArray = (Change<E>[]) Array.newInstance(Change.class, sequentialLength);
                System.arraycopy(allChanges, lastIndex + 1, subArray, 0, sequentialLength);
                // ...and sequentialises them
                currentChange = subArray[0].toSequential(subArray);
            }
            // if there are no changes to sequentialise...
            else {
                // ...directly updates te current change
                currentChange = allChanges[i];
            }

            result = currentChange.applyToImpl(result); // applies the current change
            lastIndex = i;                                 // saves the value of i
        }

        // returns the resulting array after all changes have been applied
        return result;
    }

    /**
     * Retrieves all linked {@link Change Changes} up to generation 0.
     * @param change ({@code Change<E>}): the original change from which the parent changes are retrieved
     * @return (Change\u003C E \u003E): all changes up to generation 0, including the original change
     */
    private Change<E>[] retrieveAllChanges(final Change<E> change) {
        // if given change is of generation 0, does not look for any parents
        if (generation == 0) return new Change[]{this};

        int i = 0;                  // number of generations back from given change
        Change<E> currentChange = change;             // the last change to be added to the array of all changes

        // array of all changes up to generation 0
        final Change<E>[] allChanges = new Change[change.generation + 1];

        // while we have not yet reached a change of generation 0...
        while (!currentChange.isFinal()) {
            allChanges [allChanges.length - 1 - i++] = currentChange; // saves the current change
            currentChange = currentChange.parent;                     // gets the previous change
        }

        // saves the last change
        allChanges[0] = currentChange;

        // returns the final array of all changes
        return allChanges;
    }

    /**
     * Checks whether a {@link Change} is of generation 0, therefore it cannot have any parent
     * @return (boolean); whether the change this method is being called on is of generation 0
     */
    private boolean isFinal() {
        return generation == 0;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    /**
     * Applies all {@link Change Changes} to this change's associated array.
     * @return (E[]): resulting array once all changes have been applied
     * @throws InaccessibleValueException if this change has no array associated to it
     */
    @Override
    public final E[] toArray() {
        if (array != null) {
            return applyTo(array);
        } else {
            throw new InaccessibleValueException("Can't use toArray, no array was specified to apply changes to");
        }
    }

    // TODO: implement this properly
    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public abstract String toString();

}
