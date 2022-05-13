package com.company.collections.changeAPI.changes.parallel;

import com.company.collections.ImmutableCollection;
import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.ParallelAdapter;
import com.company.collections.changeAPI.changes.parallel.remove.ParallelRemoveAll;
import com.company.collections.changeAPI.changes.parallel.remove.ParallelRemoveFirst;
import com.company.collections.changeAPI.changes.parallel.replace.ParallelReplaceAll;
import com.company.collections.changeAPI.changes.parallel.replace.ParallelReplaceAllIf;
import com.company.collections.changeAPI.changes.parallel.replace.ParallelReplaceFirstOrLast;
import com.company.collections.changeAPI.changes.parallel.retain.ParallelRetainAll;
import com.company.collections.changeAPI.changes.parallel.retain.ParallelRetainFirst;
import com.company.collections.changeAPI.changes.singlethread.functions.FunctionalChange;
import com.company.collections.changeAPI.changes.singlethread.functions.Functions;
import com.company.collections.changeAPI.changes.singlethread.remove.RemoveAt;
import com.company.collections.changeAPI.changes.singlethread.replace.ReplaceAt;
import com.company.collections.changeAPI.changes.singlethread.replace.ReplaceFirstIf;
import com.company.collections.changeAPI.changes.singlethread.replace.ReplaceLastIf;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ParallelChange<E> extends Change<E> {

    // ====================================
    //               FIELDS
    // ====================================

    protected final int threadCount;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ParallelChange(
            final Class<E> clazz
    ) {
        super(clazz);
        this.threadCount = getAvailableThreadCount();
    }

    public ParallelChange(
            final Class<E> clazz,
            final int threadCount
    ) {
        super(clazz);
        this.threadCount = threadCount;
    }

    public ParallelChange(
            final Class<E> clazz,
            final int threadCount,
            final Change<E> parent
    ) {
        super(
                clazz,
                parent
        );
        this.threadCount = threadCount;
    }

    public ParallelChange(
            final Class<E> clazz,
            final int threadCount,
            final Change<E> parent,
            final E[] array
    ) {
        super(
                clazz,
                parent,
                array
        );
        this.threadCount = threadCount;
    }

    // ====================================
    //           MULTITHREADING
    // ====================================

    protected static int getAvailableThreadCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public abstract ParallelChange<E> setCoreCount(final int coreCount);

    public int getCoreCount() {
        return threadCount;
    }

    // ====================================
    //              REMOVING
    // ====================================

    public ParallelRemoveFirst<E> removeFirst(Object... objects) {
        return new ParallelRemoveFirst<>(clazz, objects, threadCount, this);
    }

    @Override
    public ParallelRemoveFirst<E> removeAll(Collection<?> c) {
        return null;
    }

    public final ParallelRemoveAll<E> removeAll(Object... elements) {
        return new ParallelRemoveAll<>(clazz, elements, threadCount, this);
    }

    public final ParallelAdapter<E> removeAt(int... indexes) {
        return new ParallelAdapter<>(clazz, threadCount, new RemoveAt<>(clazz, indexes), this);
    }

    // ====================================
    //             REPLACING
    // ====================================

    public final ParallelAdapter<E> replaceAt(Object... objects) {
        return new ParallelAdapter<>(clazz, threadCount, new ReplaceAt<>(clazz, objects), this);
    }

    public final ParallelReplaceAllIf<E> replaceAll(
            @NotNull final Predicate<? super E> filter,
            @Nullable final E value
    ) {
        return new ParallelReplaceAllIf<>(clazz, threadCount, filter, value, this);
    }

    @SafeVarargs
    public final ParallelReplaceAll<E> replaceAll(E... values) {
        return new ParallelReplaceAll<>(clazz, threadCount, values, this);
    }

    public final ParallelAdapter<E> replaceFirst(
            @NotNull final Predicate<? super E> filter,
            @Nullable final E value
    ) {
        return new ParallelAdapter<>(clazz, threadCount, new ReplaceFirstIf<>(clazz, filter, value), this);
    }

    @SafeVarargs
    public final ParallelReplaceFirstOrLast<E> replaceFirst(E... values) {
        return new ParallelReplaceFirstOrLast<>(clazz, threadCount, values, this);
    }

    public final ParallelAdapter<E> replaceLast(
            @NotNull final Predicate<? super E> filter,
            @Nullable final E value
    ) {
        return new ParallelAdapter<>(clazz, threadCount, new ReplaceLastIf<>(clazz, filter, value), this);
    }

    @SafeVarargs
    public final ParallelReplaceFirstOrLast<E> replaceLast(E... values) {
        return new ParallelReplaceFirstOrLast<>(clazz, threadCount, values, true, this);
    }

    // ====================================
    //              RETAINING
    // ====================================

    @Override
    public ParallelRetainFirst<E> retainFirst(Object... o) {
        return new ParallelRetainFirst<>(clazz, threadCount, o, this);
    }

    @Override
    public ImmutableCollection<E> retainFirst(Collection<?> c) {
        return new ParallelRetainFirst<>(clazz, threadCount, c.toArray(), this);
    }

    @Override
    public ParallelRetainAll<E> retainAll(Collection<?> c) {
        return new ParallelRetainAll<>(clazz, threadCount, c.toArray(), this);
    }

    @Override
    public ParallelRetainAll<E> retainAll(Object... o) {
        return new ParallelRetainAll<>(clazz, threadCount, o, this);
    }

    // ====================================
    //              CLEARING
    // ====================================

    @Override
    public ParallelAdapter<E> clear() {
        return new ParallelAdapter<>(clazz, threadCount, new FunctionalChange<>(clazz, Functions.clear()), this);
    }

    // ====================================
    //              SORTING
    // ====================================

    public final ParallelAdapter<E> sorted() {
        return new ParallelAdapter<>(clazz, threadCount, new FunctionalChange<>(clazz, Functions.sort()), this);
    }

    public final ParallelAdapter<E> sorted(final Comparator<E> comparator) {
        return new ParallelAdapter<>(clazz, threadCount, new FunctionalChange<>(clazz, Functions.sort(comparator)), this);
    }

    // ====================================
    //               UNIQUE
    // ====================================

    public final ParallelAdapter<E> unique() {
        final Function<E[], E[]> unique = array -> ArrayUtil.parallelRetainDistinct(array, threadCount);
        final Change<E> change = new FunctionalChange<>(clazz, unique, this);
        return new ParallelAdapter<>(clazz, threadCount, change, this);
    }

    public final ParallelAdapter<E> unique(final Comparator<E> comparator) {
        final Function<E[], E[]> unique = array -> ArrayUtil.parallelRetainDistinct(array, threadCount, comparator);
        final Change<E> change = new FunctionalChange<>(clazz, array -> ArrayUtil.retainDistinct(array, comparator), this);
        return new ParallelAdapter<>(clazz, threadCount, change, this);
    }

    // ====================================
    //             FUNCTIONS
    // ====================================

    public final ParallelAdapter<E> forEach(final Function<E, E> function) {
        return new ParallelAdapter<>(clazz, threadCount, new FunctionalChange<>(clazz, Functions.forEach(function)), this);
    }

    @Override
    public ImmutableCollection<E> add(E e) {
        return null;
    }

    @Override
    public ImmutableCollection<E> addAll(Collection<? extends E> c) {
        return null;
    }

    @Override
    public ImmutableCollection<E> removeIf(Predicate<? super E> filter) {
        return null;
    }
}
