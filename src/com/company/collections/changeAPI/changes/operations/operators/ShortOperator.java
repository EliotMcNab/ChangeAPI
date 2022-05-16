package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class ShortOperator extends Operator<Short> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY, MULTIPLY_NUM, DIVIDE, DIVIDE_NUM};
    }

    @Override
    public Short add(@NotNull Short a, @NotNull Short b) throws OperationNotSupportedException {
        return (short) (a + b);
    }

    @Override
    public Short sub(@NotNull Short a, @NotNull Short b) throws OperationNotSupportedException {
        return (short) (a - b);
    }

    @Override
    public Short mult(@NotNull Short a, @NotNull Short b) throws OperationNotSupportedException {
        return (short) (a * b);
    }

    @Override
    public Short mult(@NotNull Short a, long num) throws OperationNotSupportedException {
        return (short) (a * num);
    }

    @Override
    public Short div(@NotNull Short a, @NotNull Short b) throws OperationNotSupportedException {
        return (short) (a / b);
    }

    @Override
    public Short div(@NotNull Short a, long num) throws OperationNotSupportedException {
        return (short) (a / num);
    }
}
