package com.alan.tdd.args.exceptions;

public class InsufficientException extends RuntimeException {

    private final String options;

    public InsufficientException(String options) {
        super(options);
        this.options = options;
    }

    public String getOption() {
        return options;
    }
}
