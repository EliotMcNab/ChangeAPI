package com.company.collections.changeAPI.changes.operations;

import org.jetbrains.annotations.NotNull;

public interface Summable<E> {

    E add(@NotNull final E element);

    E sub(@NotNull final E element);
}
