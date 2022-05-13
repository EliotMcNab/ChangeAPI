package com.company.collections.changeAPI.errors;

public class InaccessibleValueException extends RuntimeException {
    public InaccessibleValueException() {
        super();
    }

    public InaccessibleValueException(final String errorMessage) {
        super(errorMessage);
    }
}
