package com.company.collections;

import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface ImmutableCollection<E> {
    int size();

    boolean isEmpty();

    boolean contains(Object o);

    Object[] toArray();

    <T> T[] toArray(T[] a);

    default <T> T[] toArray(IntFunction<T[]> generator) {
        return toArray(generator.apply(0));
    }

    ImmutableCollection<E> add(E e);

    ImmutableCollection<E> removeFirst(Object... objects);

    boolean containsAll(Collection<?> c);

    ImmutableCollection<E> addAll(Collection<? extends E> c);

    ImmutableCollection<E> removeAll(Collection<?> c);

    ImmutableCollection<E> removeIf(Predicate<? super E> filter);

    ImmutableCollection<E> retainFirst(Object... o);

    ImmutableCollection<E> retainFirst(Collection<?> c);

    ImmutableCollection<E> retainAll(Collection<?> c);

    ImmutableCollection<E> retainAll(Object... o);

    ImmutableCollection<E> clear();

    /*default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0);
    }

    Stream<E> stream();

    Stream<E> parallelStream();*/

}
