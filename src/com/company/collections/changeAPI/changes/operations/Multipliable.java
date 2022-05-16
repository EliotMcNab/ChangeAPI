package com.company.collections.changeAPI.changes.operations;

import org.jetbrains.annotations.NotNull;

public interface Multipliable<E> {

    E mult(@NotNull final E element);

    E mult(final int num);
}
