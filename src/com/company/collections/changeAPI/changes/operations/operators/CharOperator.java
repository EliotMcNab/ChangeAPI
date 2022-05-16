package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class CharOperator extends Operator<Character> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY};
    }

    @Override
    public Character add(@NotNull Character a, @NotNull Character b) throws OperationNotSupportedException {
        return (char) (Character.getNumericValue(a) + Character.getNumericValue(b));
    }

    @Override
    public Character sub(@NotNull Character a, @NotNull Character b) throws OperationNotSupportedException {
        return (char) (Character.getNumericValue(a) - Character.getNumericValue(b));
    }

    @Override
    public Character mult(@NotNull Character a, long num) throws OperationNotSupportedException {
        return (char) (Math.toIntExact(Character.getNumericValue(a) * num));
    }
}
