package com.company.collections.changeAPI.changes.operations;

import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

public abstract class Operator<E> {

    public enum OPERATIONS {
        ADD,
        SUBTRACT,
        MULTIPLY,
        MULTIPLY_NUM,
        DIVIDE,
        DIVIDE_NUM
    }

    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[0];
    }

    public E add(@NotNull final E a, @NotNull final E b) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Addition is not supported by this operator"
        );
    }

    public E sub(@NotNull final E a, @NotNull final E b) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Subtraction is not supported by this operator"
        );
    }

    public E mult(@NotNull final E a, @NotNull final E b) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Multiplication is not supported by this operator"
        );
    }

    public E mult(@NotNull final E a, final long num) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Scalar multiplication is not supported by this operator"
        );
    }

    public E div(@NotNull final E a, @NotNull final E b) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Division is not supported by this operator"
        );
    }

    public E div(@NotNull final E a, final long num) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                "Scalar division is not supported by this operator"
        );
    }

}
