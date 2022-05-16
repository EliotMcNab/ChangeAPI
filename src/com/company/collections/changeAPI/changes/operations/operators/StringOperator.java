package com.company.collections.changeAPI.changes.operations.operators;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.operations.Operator;
import com.company.collections.changeAPI.errors.OperationNotSupportedException;
import com.company.utilities.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import static com.company.collections.changeAPI.changes.operations.Operator.OPERATIONS.*;

public class StringOperator extends Operator<String> {

    @Override
    public OPERATIONS[] supportedOperation() {
        return new OPERATIONS[]{ADD, SUBTRACT, MULTIPLY};
    }

    @Override
    public String add(@NotNull String a, @NotNull String b) throws OperationNotSupportedException {
        return a + b;
    }

    @Override
    public String sub(@NotNull String a, @NotNull String b) throws OperationNotSupportedException {
        final Character[] array = ArrayUtil.box(ArrayUtil.toCharArray(a));
        final Character[] toRemove = ArrayUtil.box(ArrayUtil.toCharArray(b));
        final Character[] result = Change.of(array).removeAll(toRemove).toArray();
        return ArrayUtil.fromCharArray(ArrayUtil.unbox(result));
    }

    @Override
    public String mult(@NotNull String a, long num) throws OperationNotSupportedException {
        return a.repeat(Math.toIntExact(num));
    }
}
