package com.company.collections.changeAPI.changes.singlethread.replace;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@link SingleThreadChange} responsible for replacing all elements at the given indexes in an array with new values. Can replace
 * elements at multiple indexes each with different values
 * @param <E> the type the SingleThreadChange operates on
 */
public class ReplaceAt<E> extends ReplaceValues<E> {

    // ====================================
    //               FIELDS
    // ====================================

    private static final Class<?>[] SEQUENTIALISEABLE = new Class<?>[]{
        ReplaceAt.class
    };

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public ReplaceAt(
            @NotNull final Class<E> clazz,
            @NotNull final Object[] toReplace
    ) {
        super(
                clazz,
                toReplace
        );
    }

    public ReplaceAt(
            @NotNull final Class<E> clazz,
            @NotNull final Object[] toReplace,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                toReplace,
                parent
        );
    }

    public ReplaceAt(
            @NotNull final Class<E> clazz,
            final int @NotNull [] indexes,
            final E @NotNull [] replacing
    ) {
        super(
                clazz,
                blend(indexes, replacing)
        );
    }

    public ReplaceAt(
            @NotNull final Class<E> clazz,
            final int @NotNull [] indexes,
            final E @NotNull [] replacing,
            @Nullable final Change<E> parent
    ) {
        super(
                clazz,
                blend(indexes, replacing),
                parent
        );
    }

    public static Object[] blend(
            final int @NotNull [] indexes,
            final Object @NotNull [] replacing
    ) {
        Objects.requireNonNull(indexes);
        Objects.requireNonNull(replacing);

        if (indexes.length != replacing.length)
            throw new IllegalArgumentException(
                "Invalid array of elements to replace, " +
                        "must have equal number of values to replace and replacing values"
        );

        final Object[] result = new Object[indexes.length * 2];

        for (int i = 0; i < result.length - 1; i += 2) {
            result[i] = indexes[i / 2];
            result[i + 1] = replacing[i / 2];
        }

        return result;
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
        return new SequentialReplaceAt<>(clazz, changes);
    }

    @Override
    protected E[] applyToImpl(E[] array) {
        final Object[] indexes = getEvenIndexes();
        final Object[] values = getOddIndexes();

        final E[] result = Arrays.copyOf(array, array.length);

        for (int i = 0; i < indexes.length; i++) {
            result[(int) indexes[i]] = (E) values[i];
        }

        return result;
    }

    // ====================================
    //          ARRAY CONVERSION
    // ====================================

    @Override
    public String toString() {
        return "ReplaceAt{toReplace=" +
                Arrays.toString(values) +
                "}";
    }
}
