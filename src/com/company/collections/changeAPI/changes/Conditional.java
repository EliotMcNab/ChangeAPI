package com.company.collections.changeAPI.changes;

import java.util.Collection;

public interface Conditional {
    boolean matches(final Object o);

    boolean allMatch(final Collection<?> c);
}
