package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;
import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.DIVIDE_NUM;

public class LongOperator extends Operator<Long> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY, MULTIPLY_NUM, DIVIDE, DIVIDE_NUM};
    }

    @Override
    public Long add(@NotNull Long a, @NotNull Long b) throws OperationNotSupportedException {
        return a + b;
    }

    @Override
    public Long sub(@NotNull Long a, @NotNull Long b) throws OperationNotSupportedException {
        return a - b;
    }

    @Override
    public Long mult(@NotNull Long a, @NotNull Long b) throws OperationNotSupportedException {
        return a * b;
    }

    @Override
    public Long mult(@NotNull Long a, long num) throws OperationNotSupportedException {
        return a * num;
    }

    @Override
    public Long div(@NotNull Long a, @NotNull Long b) throws OperationNotSupportedException {
        return a / b;
    }

    @Override
    public Long div(@NotNull Long a, long num) throws OperationNotSupportedException {
        return a / num;
    }
}
