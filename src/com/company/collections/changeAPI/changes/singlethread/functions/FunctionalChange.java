package com.company.collections.changeAPI.changes.singlethread.functions;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class FunctionalChange<E> extends SingleThreadChange<E> {

    // ====================================
    //               FIELDS
    // ====================================

    protected final Function<E[], E[]> function;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public FunctionalChange(
            final Class<E> clazz,
            final Function<E[], E[]> function
    ) {
        super(clazz);
        this.function = function;
    }

    public FunctionalChange(
            final Class<E> clazz,
            final Function<E[], E[]> function,
            final Change<E> parent
    ) {
        super(
                clazz,
                parent
        );
        this.function = function;
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return false;
    }

    @Override
    protected Change<E> toSequential(Change<E>[] changes) {
        return null;
    }

    @Override
    protected E[] applyToImpl(@NotNull E[] array) {
        return function.apply(array);
    }

    // ====================================
    //             ACCESSORS
    // ====================================

    public Function<E[], E[]> getFunction() {
        return function;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "FunctionalChange{function=" +
                function +
                "}";
    }
}
