package com.alan.tdd.args.exceptions;

public class IllegalOptionException  extends RuntimeException {

    private String parameter;

    public IllegalOptionException(String parameter) {
        super(parameter);
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
