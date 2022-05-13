package com.company.collections.changeAPI.generation;

import java.lang.reflect.Array;

public interface Generator<E> {
    E generate();

    default E[] generateArray(final Class<E> clazz, final int length) {
        // resulting array
        final E[] result = (E[]) Array.newInstance(clazz, length);

        // generates every element in the array
        for (int i = 0; i < result.length; i++) {
            result[i] = generate();
        }

        // returns the final array
        return result;
    }

    default int[] generateIntArray(final int length) {
        // resulting array
        final int[] result = new int[length];

        // generates every element in the array
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) generate();
        }

        // returns the final array
        return result;
    }

    default char[] generateCharArray(final int length) {
        // resulting array
        final char[] result = new char[length];

        // generates every element in the array
        for (int i = 0; i < result.length; i++) {
            result[i] = (char) generate();
        }

        // returns the final array
        return result;
    }

    Generator<Double> RANDOM = Math::random;
    Generator<Double> EMPTY = () -> 0.0;
    Generator<Double> MINUS_INFINITY = () -> Double.NEGATIVE_INFINITY;
    Generator<Double> POSITIVE_INFINITY = () -> Double.POSITIVE_INFINITY;
}
