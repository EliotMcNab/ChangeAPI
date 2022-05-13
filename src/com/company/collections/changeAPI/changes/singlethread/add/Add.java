package com.company.collections.changeAPI.changes.singlethread.add;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

/**
 * {@link SingleThreadChange} responsible for adding elements to an array
 * @param <E> the type the SingleThreadChange operates on
 */
public class Add<E> extends AddBase<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISATBLE = new Class<?>[]{Add.class};

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public Add(
            final Class<E> clazz,
            final E[] toAdd
    ) {
        super(
                clazz,
                toAdd
        );
    }

    public Add(
            final Class<E> clazz,
            final E[] toAdd,
            final Change<E> parent
    ) {
        super(
                clazz,
                toAdd,
                parent
        );
    }

    public Add(
            final Class<E> clazz,
            final Collection<? extends E> c
    ) {
        super(
                clazz,
                (E[]) c.toArray(),
                null
        );
    }

    public Add(
            final Class<E> clazz,
            final Collection<? extends E> c,
            final Change<E> parent
    ) {
        super(
                clazz,
                (E[]) c.toArray(),
                parent
        );
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return Arrays.asList(SEQUENTIALISATBLE).contains(change.getClass());
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        final E[] result = (E[]) Array.newInstance(clazz, array.length + toAdd.length);
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAdd, 0, result, array.length, toAdd.length);
        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "Add{toAdd=" +
                Arrays.toString(toAdd) +
                "}";
    }
}
