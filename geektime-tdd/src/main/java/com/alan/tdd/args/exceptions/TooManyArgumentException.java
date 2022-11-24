package com.alan.tdd.args.exceptions;

public class TooManyArgumentException extends RuntimeException {

    private final String options;


    public TooManyArgumentException(String options) {
        super(options);
        this.options = options;
    }

    public String getOption() {
        return options;
    }
}
