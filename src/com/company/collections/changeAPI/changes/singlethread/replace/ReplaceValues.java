package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.utilities.ArrayUtil;

public abstract class ReplaceValues<E> extends ReplaceBase<E> {

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ReplaceValues(
            final Class<E> clazz,
            final Object[] toReplace
    ) {
        super(
                clazz,
                toReplace,
                null
        );
    }

    public ReplaceValues(
            final Class<E> clazz,
            final Object[] toReplace,
            final Change<E> parent
    ) {
        super(
                clazz,
                toReplace,
                null,
                parent
        );
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    public Object[] getEvenIndexes() {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException(
                    "Invalid array of elements to replace, " +
                            "must have equal number of values to replace and replacing values"
            );

        return ArrayUtil.getAtMultiples(values, 2);
    }

    public Object[] getOddIndexes() {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException(
                    "Invalid array of elements to replace, " +
                            "must have equal number of values to replace and replacing values"
            );

        return ArrayUtil.getAtNonMultiples(values, 2);
    }

}
