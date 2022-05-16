package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class DoubleOperator extends Operator<Double> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY, MULTIPLY_NUM, DIVIDE, DIVIDE_NUM};
    }

    @Override
    public Double add(@NotNull Double a, @NotNull Double b) throws OperationNotSupportedException {
        return a + b;
    }

    @Override
    public Double sub(@NotNull Double a, @NotNull Double b) throws OperationNotSupportedException {
        return a - b;
    }

    @Override
    public Double mult(@NotNull Double a, @NotNull Double b) throws OperationNotSupportedException {
        return a * b;
    }

    @Override
    public Double mult(@NotNull Double a, long num) throws OperationNotSupportedException {
        return a * num;
    }

    @Override
    public Double div(@NotNull Double a, @NotNull Double b) throws OperationNotSupportedException {
        return a / b;
    }

    @Override
    public Double div(@NotNull Double a, long num) throws OperationNotSupportedException {
        return a / num;
    }
}
