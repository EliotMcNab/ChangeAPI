package com.company.collections.changeAPI.changes.operations;

import org.jetbrains.annotations.NotNull;

public interface Divisible<E> {

    E div(@NotNull final E element);

    E div(final int num);

    E mult(@NotNull final E element);

    E mult(final int num);

}
