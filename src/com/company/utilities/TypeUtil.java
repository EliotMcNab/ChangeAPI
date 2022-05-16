package com.company.utilities;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.changes.operations.operators.*;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TypeUtil {
    public static boolean isBoxed(@NotNull final Object[] array) {
        Objects.requireNonNull(array);

        final Class<?> clazz = array.getClass().getComponentType();

        return clazz == Byte.class       ||
                clazz == Short.class     ||
                clazz == Integer.class   ||
                clazz == Long.class      ||
                clazz == Float.class     ||
                clazz == Double.class    ||
                clazz == Boolean.class   ||
                clazz == Character.class ||
                clazz == String.class;
    }

    public static <T> Operator<T> getBasicOperator(@NotNull final Class<T> clazz) {
        if (clazz == Byte.class)    return (Operator<T>) new ByteOperator();
        if (clazz == Short.class)   return (Operator<T>) new ShortOperator();
        if (clazz == Integer.class) return (Operator<T>) new IntegerOperator();
        if (clazz == Long.class)    return (Operator<T>) new LongOperator();
        if (clazz == Float.class)   return (Operator<T>) new FloatOperator();
        if (clazz == Double.class)  return (Operator<T>) new DoubleOperator();
        if (clazz == String.class)  return (Operator<T>) new StringOperator();

        throw new OperationNotSupportedException(
                "Operating on non-basic or non-boxed type " + clazz + " requires a custom operator"
        );
    }
}
