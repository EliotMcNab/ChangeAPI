package com.company.collections.changeAPI.changes.singlethread.add;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Sequential implementation of the algorithms use in {@link Add}
 * @param <E> the type the {@link SingleThreadChange} operates on
 */
public class
SequentialAdd<E> extends AddBase<E> {

    // =====================================
    //               FIELDS
    // =====================================
    private final Change<E>[] changes;
    private E[] allToAdd;
    private boolean concatenated = false;
    private final int totalLength;

    // =====================================
    //            CONSTRUCTOR
    // =====================================

    public SequentialAdd(
            final Class<E> clazz,
            final Change<E>[] changes
    ) {
        super(
                clazz,
                null
        );
        this.changes = changes;                             // saves the changes
        this.totalLength = determineTotalChangeLength(changes); // determines the total number of elements to add
    }

    // ====================================
    //          APPLYING CHANGES
    // ====================================

    @Override
    protected boolean canSequentialise(Change<E> change) {
        return false;
    }

    private int determineTotalChangeLength(
            final Change<E>[] changes
    ) {
        int length = 0;
        for (Change<E> change : changes) {
            length += ((AddBase<E>)change).toAdd.length;
        }
        return length;
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        if (changes.length == 0) return array;

        final E[] result = (E[]) Array.newInstance(clazz, array.length + totalLength);
        System.arraycopy(array, 0, result, 0, array.length);

        int k = array.length;
        for (Change<E> change : changes) {
            AddBase<E> add = (AddBase<E>) change;
            System.arraycopy(add.toAdd, 0, result, k, add.toAdd.length);
            k += add.toAdd.length;
        }

        return Arrays.copyOf(result, k);
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    /**
     * Concatenates the toAdd array in every sequential {@link SingleThreadChange Changes}
     * @return (Object[])
     */
    private E[] concatenateChanges() {
        if (changes.length == 0 || ((AddBase<E>) changes[0]).toAdd.length == 0)
            return (E[]) Array.newInstance(clazz, 0);
        else if (concatenated) return allToAdd;

        final E[] result = (E[]) Array.newInstance(clazz , totalLength);

        int k = 0;
        for (Change<E> change : changes) {
            AddBase<E> add = (AddBase<E>) change;
            System.arraycopy(add.toAdd, 0, result, k, add.toAdd.length);
            k += add.toAdd.length;
        }

        concatenated = true;
        allToAdd = Arrays.copyOf(result, k);

        return allToAdd;
    }

    @Override
    public String toString() {
        return Arrays.toString(concatenated ? allToAdd : concatenateChanges());
    }

}
