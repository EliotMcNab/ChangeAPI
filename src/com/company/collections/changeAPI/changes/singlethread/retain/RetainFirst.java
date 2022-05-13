package com.company.collections.changeAPI.changes.singlethread.retain;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.utilities.ArrayUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@link SingleThreadChange} responsible for retaining the first instances of given elements in an array. Can retain multiple
 * elements at once
 * @param <E> the type the SingleThreadChange operates on
 */
public class RetainFirst<E> extends RetainBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
            RetainAll.class,
            RetainFirst.class
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RetainFirst(
            final Class<E> clazz,
            final Object[] toRetain
    ) {
        super(
                clazz,
                toRetain,
                null
        );
    }

    public RetainFirst(
            final Class<E> clazz,
            final Object[] toRetain,
            final Change<E> parent
    ) {
        super(
                clazz,
                toRetain,
                null,
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
    protected E[] applyToImpl(E[] array) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(clazz);

        // determines the index of all the element to retain & sorts them
        final int[] indexes = ArrayUtil.quickFindFirst(array, toRetain);
        return ArrayUtil.retainAt(array, indexes);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "RetainFirst{toRetain=" +
                Arrays.toString(toRetain) +
                "}";
    }
}
