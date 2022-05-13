package com.company.collections.changeAPI.changes.singlethread.remove;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * {@link SingleThreadChange} responsible for removing elements in an array if they match a given {@link Predicate}. Removes all
 * instances that match the predicate
 * @param <E> the type the SingleThreadChange operates on
 */
public class RemoveIf<E> extends RemoveBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
            RemoveIf.class,
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RemoveIf(
            final Class<E> clazz,
            final Predicate<? super E> filter
    ) {
        super(
                clazz,
                null,
                filter,
                new int[0]
        );
    }

    public RemoveIf(
            final Class<E> clazz,
            final Predicate<? super E> filter,
            final Change<E> parent
    ) {
        super(
                clazz,
                null,
                filter,
                new int[0],
                parent
        );
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return Arrays.asList(SEQUENTIALISEABLE).contains(change.getClass());
    }

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return new SequentialRemoveIf<>(clazz, changes);
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        return ArrayUtil.removeAt(array, ArrayUtil.findAllMatches(array, filter));
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "RemoveIf{predicate=" +
                filter +
                "}";
    }
}
