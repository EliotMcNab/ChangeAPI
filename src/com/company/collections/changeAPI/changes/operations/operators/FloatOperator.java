package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class FloatOperator extends Operator<Float> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY, MULTIPLY_NUM, DIVIDE, DIVIDE_NUM};
    }

    @Override
    public Float add(@NotNull Float a, @NotNull Float b) throws OperationNotSupportedException {
        return a + b;
    }

    @Override
    public Float sub(@NotNull Float a, @NotNull Float b) throws OperationNotSupportedException {
        return a - b;
    }

    @Override
    public Float mult(@NotNull Float a, @NotNull Float b) throws OperationNotSupportedException {
        return a * b;
    }

    @Override
    public Float mult(@NotNull Float a, long num) throws OperationNotSupportedException {
        return a * num;
    }

    @Override
    public Float div(@NotNull Float a, @NotNull Float b) throws OperationNotSupportedException {
        return a / b;
    }

    @Override
    public Float div(@NotNull Float a, long num) throws OperationNotSupportedException {
        return a / num;
    }
}
