package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class IntegerOperator extends Operator<Integer> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY, MULTIPLY_NUM, DIVIDE, DIVIDE_NUM};
    }

    @Override
    public Integer add(@NotNull Integer a, @NotNull Integer b) throws OperationNotSupportedException {
        return Integer.sum(a, b);
    }

    @Override
    public Integer sub(@NotNull Integer a, @NotNull Integer b) throws OperationNotSupportedException {
        return super.sub(a, -b);
    }

    @Override
    public Integer mult(@NotNull Integer a, @NotNull Integer b) throws OperationNotSupportedException {
        return a * b;
    }

    @Override
    public Integer mult(@NotNull Integer a, long num) throws OperationNotSupportedException {
        return Math.toIntExact(a * num);
    }

    @Override
    public Integer div(@NotNull Integer a, @NotNull Integer b) throws OperationNotSupportedException {
        return a / b;
    }

    @Override
    public Integer div(@NotNull Integer a, long num) throws OperationNotSupportedException {
        return Math.toIntExact(a / num);
    }
}
