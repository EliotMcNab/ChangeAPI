package com.company.collections.changeAPI.errors;

public class OperationNotSupportedException extends RuntimeException {
    public OperationNotSupportedException() {
        super();
    }

    public OperationNotSupportedException(final String errorMessage) {
        super(errorMessage);
    }
}
