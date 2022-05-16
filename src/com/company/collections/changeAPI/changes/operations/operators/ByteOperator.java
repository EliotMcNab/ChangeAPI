package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class ByteOperator extends Operator<Byte> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY, MULTIPLY_NUM, DIVIDE, DIVIDE_NUM};
    }

    @Override
    public Byte add(@NotNull Byte a, @NotNull Byte b) throws OperationNotSupportedException {
        return (byte) (a + b);
    }

    @Override
    public Byte sub(@NotNull Byte a, @NotNull Byte b) throws OperationNotSupportedException {
        return (byte) (a - b);
    }

    @Override
    public Byte mult(@NotNull Byte a, @NotNull Byte b) throws OperationNotSupportedException {
        return (byte) (a * b);
    }

    @Override
    public Byte mult(@NotNull Byte a, long num) throws OperationNotSupportedException {
        return (byte) (a * num);
    }

    @Override
    public Byte div(@NotNull Byte a, @NotNull Byte b) throws OperationNotSupportedException {
        return (byte) (a / b);
    }

    @Override
    public Byte div(@NotNull Byte a, long num) throws OperationNotSupportedException {
        return (byte) (a / num);
    }
}
